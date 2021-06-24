package io.github.edmm.plugins.puppet;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.yaml.EDMMiYamlTransformer;
import io.github.edmm.exporter.OpenTOSCAConnector;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;
import io.github.edmm.plugins.puppet.api.AuthenticatorImpl;
import io.github.edmm.plugins.puppet.api.PuppetApiInteractor;
import io.github.edmm.plugins.puppet.model.Fact;
import io.github.edmm.plugins.puppet.model.Master;
import io.github.edmm.plugins.puppet.model.PuppetResourceStatus;
import io.github.edmm.plugins.puppet.model.PuppetState;
import io.github.edmm.plugins.puppet.model.Report;
import io.github.edmm.plugins.puppet.model.ResourceEventEntry;
import io.github.edmm.plugins.puppet.typemapper.MySQLMapper;
import io.github.edmm.plugins.puppet.typemapper.TomcatMapper;
import io.github.edmm.plugins.puppet.typemapper.WebApplicationMapper;
import io.github.edmm.plugins.puppet.util.PuppetNodeHandler;
import io.github.edmm.util.CastUtil;
import io.github.edmm.util.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuppetInstancePlugin extends AbstractLifecycleInstancePlugin<PuppetInstancePlugin> {

    private final static Logger logger = LoggerFactory.getLogger(PuppetInstancePlugin.class);
    private static final SourceTechnology PUPPET = SourceTechnology.builder().id("puppet").name("Puppet").build();

    // puppet master info
    private final String user;
    private final String ip;
    private final String privateKeyLocation;
    private final Integer port;
    private final String operatingSystem;
    private final String operatingSystemRelease;

    private final DeploymentInstance deploymentInstance = new DeploymentInstance();
    private final TOSCATransformer toscaTransformer;
    private Master master;

    public PuppetInstancePlugin(
        InstanceTransformationContext context,
        String user,
        String ip,
        String privateKeyLocation,
        Integer port,
        String operatingSystem,
        String operatingSystemRelease) {
        super(context);
        this.user = user;
        this.ip = ip;
        this.privateKeyLocation = privateKeyLocation;
        this.port = port;
        this.operatingSystem = operatingSystem;
        this.operatingSystemRelease = operatingSystemRelease;

        this.toscaTransformer = new PuppetToscaTransformer(
            new MySQLMapper(), new WebApplicationMapper(), new TomcatMapper()
        );
    }

    @Override
    public void prepare() {
        AuthenticatorImpl authenticator = new AuthenticatorImpl(new Master(this.user,
            this.ip,
            this.privateKeyLocation,
            this.port));
        authenticator.authenticate();
        this.master = authenticator.getMaster();
    }

    @Override
    public void getModels() {
        PuppetApiInteractor apiInteractor = new PuppetApiInteractor(this.master);
        this.master = apiInteractor.getDeployment();
        this.master.setOperatingSystem(this.operatingSystem);
        this.master.setOperatingSystemRelease(this.operatingSystemRelease);
    }

    @Override
    public void transformToEDMMi() {
        this.deploymentInstance.setId(this.master.getHostName() + "-" + this.master.getId());
        this.deploymentInstance.setCreatedAt(this.master.getCreatedAtTimestamp());
        this.deploymentInstance.setName(this.master.getHostName());
        this.deploymentInstance.setVersion(this.master.getPuppetVersion());
        this.deploymentInstance.setComponentInstances(PuppetNodeHandler.getComponentInstances(this.master));
        this.deploymentInstance.setState(PuppetState.getDeploymentInstanceState(this.master));
    }

    @Override
    public void transformDirectlyToTOSCA() {
        TTopologyTemplate topologyTemplate = new TTopologyTemplate();

        master.getNodes().forEach(node -> {
            Fact nodeOS = node.getFactByName("operatingSystem".toLowerCase());
            Fact nodeOSRelease = node.getFactByName("operatingSystemRelease".toLowerCase());

            // TODO temporary fix -> remove
            if (nodeOS == null || nodeOSRelease == null) {
                return;
            }

            TNodeType vmType = toscaTransformer.getComputeNodeType(nodeOS.getValue().toString(),
                nodeOSRelease.getValue().toString());
            TNodeTemplate vm = ModelUtilities.instantiateNodeTemplate(vmType);
            vm.setId(node.getCertname());
            vm.setName(node.getCertname());

            TEntityTemplate.Properties properties = vm.getProperties();
            if (properties != null && properties.getKVProperties() != null) {
                Map<String, String> vmProps = properties.getKVProperties();
                vmProps.put(Constants.VMIP, node.getFactByName("ipaddress").getValue().toString());
                vmProps.put(Constants.VM_INSTANCE_ID, node.getCertname());
                vmProps.put(Constants.VM_PRIVATE_KEY, this.master.getPrivateKey());
                vmProps.put(Constants.VM_USER_NAME, nodeOS.getValue().toString().toLowerCase());
                vmProps.put(Constants.STATE, Constants.RUNNING);

                Map<String, String> masterProps = new HashMap<>();
                masterProps.put(Constants.PUPPET_ENV, node.getReport_environment());
                masterProps.put(Constants.PUPPET_MASTER, this.master.getIp());
                masterProps.put(Constants.PUPPET_MASTER_KEY, this.master.getPrivateKey());
                masterProps.put(Constants.PUPPET_MASTER_USER, this.master.getUser());
                vmProps.putAll(masterProps);
                populateNodeTemplateProperties(vm, vmProps);

                Fact ec2_metadata = node.getFactByName("ec2_metadata");
                if (ec2_metadata != null) {
                    Map<String, Object> values = CastUtil.safelyCastToStringObjectMap(ec2_metadata.getValue());
                    if (values.get("instance-type") != null) {
                        vmProps.put(Constants.VMTYPE, values.get("instance-type").toString());
                    }
                }

                properties.setKVProperties(vmProps);
            }

            topologyTemplate.addNodeTemplate(vm);

            if (node.getFactByName("productName".toLowerCase()) != null) {
                Fact hypervisorFacts = node.getFactByName("productName".toLowerCase());
                TNodeType hypervisorType = toscaTransformer.getComputeNodeType(hypervisorFacts.getValue().toString(),
                    "");
                TNodeTemplate hypervisor = ModelUtilities.instantiateNodeTemplate(hypervisorType);
                hypervisor.setName(hypervisorFacts.getValue().toString());
                this.populateNodeTemplateProperties(hypervisor);
                topologyTemplate.addNodeTemplate(hypervisor);
                ModelUtilities.createRelationshipTemplateAndAddToTopology(vm, hypervisor,
                    ToscaBaseTypes.hostedOnRelationshipType, topologyTemplate);
            }

            Set<String> environments = new HashSet<>();
            List<Report> reports = PuppetNodeHandler.identifyRelevantReports(this.master, node.getCertname());
            reports.stream()
                .peek(report -> environments.add(report.getEnvironment()))
                .map(report -> report.getResource_events().getData())
                .flatMap(entry -> entry.stream()
                    .filter(event -> event.getStatus() == PuppetResourceStatus.success)
                    .sorted(Comparator.comparing(ResourceEventEntry::getTimestamp))
                    .map(event -> {
                        // :: separates class and operation
                        int index = event.getContaining_class().lastIndexOf(Constants.DELIMITER);
                        return index > 0
                            ? event.getContaining_class().substring(0, index)
                            : event.getContaining_class();
                    }))
                .distinct()
                .peek(identifiedComponent -> logger.info("Identified component '{}' on stack '{}'",
                    identifiedComponent,
                    node.getCertname()))
                .forEach(identifiedComponent -> {
                    TNodeType softwareNodeType = toscaTransformer.getSoftwareNodeType(identifiedComponent, "");

                    TNodeTemplate softwareNode = ModelUtilities.instantiateNodeTemplate(softwareNodeType);
                    String normalizedName = identifiedComponent.replaceAll("(\\s)|(:)|(\\.)", "_");
                    softwareNode.setId(node.getCertname() + "-" +
                        normalizedName);
                    softwareNode.setName(normalizedName);
                    this.populateNodeTemplateProperties(softwareNode);

                    topologyTemplate.addNodeTemplate(softwareNode);

                    if (this.toscaTransformer.getTransformTypePlugins().stream()
                        .noneMatch(typeTransformer -> typeTransformer.refineHost(softwareNode, vm, topologyTemplate))
                    ) {
                        ModelUtilities.createRelationshipTemplateAndAddToTopology(softwareNode, vm,
                            ToscaBaseTypes.hostedOnRelationshipType, topologyTemplate);
                    }
                });

            if (environments.size() > 0) {
                Map<String, String> vmProperties = Optional.ofNullable(properties)
                    .map(TEntityTemplate.Properties::getKVProperties)
                    .orElseGet(LinkedHashMap::new);
                vmProperties.put("PuppetEnvironments", String.join(",", environments));
                populateNodeTemplateProperties(vm, vmProperties);
            }
        });

        TServiceTemplate serviceTemplate = new TServiceTemplate.Builder("puppet-" + this.master.getId(),
            topologyTemplate)
            .setName("puppet-" + this.master.getId())
            .setTargetNamespace("http://opentosca.org/retrieved/instances")
            .addTags(new TTags.Builder()
                .addTag("deploymentTechnology", PUPPET.getName())
                .build()
            ).build();

        updateGeneratedServiceTemplate(serviceTemplate);
    }

    @Override
    public void storeTransformedTOSCA() {
        Optional.ofNullable(retrieveGeneratedServiceTemplate()).ifPresent(toscaTransformer::save);
    }

    private void populateNodeTemplateProperties(TNodeTemplate nodeTemplate) {
        Map<String, String> additionalProperties = new HashMap<>();
        additionalProperties.put("puppetInstanceType", nodeTemplate.getName());
        additionalProperties.put("State", "Running");
        this.populateNodeTemplateProperties(nodeTemplate, additionalProperties);
    }

    private void populateNodeTemplateProperties(TNodeTemplate nodeTemplate, Map<String, String> additionalProperties) {
        if (nodeTemplate.getProperties() != null && nodeTemplate.getProperties().getKVProperties() != null) {
            nodeTemplate.getProperties().getKVProperties().entrySet().stream()
                .filter(entry -> !additionalProperties.containsKey(entry.getKey())
                    || additionalProperties.get(entry.getKey()).isEmpty())
                .forEach(entry ->
                    additionalProperties.put(entry.getKey(), entry.getValue() != null && !entry.getValue().isEmpty()
                        ? entry.getValue()
                        : "get_input: " + entry.getKey() + "_" + nodeTemplate.getId()
                        .replaceAll("(\\s)|(:)|(\\.)", "_"))
                );
        }

        // workaround to set new properties
        nodeTemplate.setProperties(new TEntityTemplate.Properties());
        nodeTemplate.getProperties().setKVProperties(additionalProperties);
    }

    @Override
    public void transformEdmmiToTOSCA() {
        ServiceTemplateInstance serviceTemplateInstance = toscaTransformer.transformEDiMMToServiceTemplateInstance(
            deploymentInstance);
        OpenTOSCAConnector.processServiceTemplateInstanceToOpenTOSCA(context.getSourceTechnology().getName(),
            serviceTemplateInstance,
            context.getOutputPath() + deploymentInstance.getName() + ".csar");
        logger.info("Transformed to OpenTOSCA Service Template Instance: {}", serviceTemplateInstance.getCsarId());
    }

    @Override
    public void createYAML() {
        EDMMiYamlTransformer EDMMiYamlTransformer = new EDMMiYamlTransformer();
        EDMMiYamlTransformer.createYamlforEDiMM(this.deploymentInstance, context.getOutputPath());
        logger.info("Saved YAML for EDMMi to {}", EDMMiYamlTransformer.getFileOutputLocation());
    }

    @Override
    public void cleanup() {
        this.master.getSession().disconnect();
    }
}
