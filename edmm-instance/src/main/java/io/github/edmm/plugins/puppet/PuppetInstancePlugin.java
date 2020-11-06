package io.github.edmm.plugins.puppet;

import java.util.LinkedHashMap;

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
import io.github.edmm.plugins.puppet.model.PuppetState;
import io.github.edmm.plugins.puppet.util.PuppetNodeHandler;
import io.github.edmm.util.Constants;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
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

    public PuppetInstancePlugin(InstanceTransformationContext context, String user, String ip, String privateKeyLocation, Integer port, String operatingSystem, String operatingSystemRelease) {
        super(context);
        this.user = user;
        this.ip = ip;
        this.privateKeyLocation = privateKeyLocation;
        this.port = port;
        this.operatingSystem = operatingSystem;
        this.operatingSystemRelease = operatingSystemRelease;
        this.toscaTransformer = new TOSCATransformer();
    }

    @Override
    public void prepare() {
        AuthenticatorImpl authenticator = new AuthenticatorImpl(new Master(this.user, this.ip, this.privateKeyLocation, this.port));
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
        this.deploymentInstance.setId(this.master.getId());
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
            Fact nodeOS = node.getFactByName("operatingsystem");
            Fact nodeOSRelease = node.getFactByName("operatingsystemrelease");

            TNodeType nodeType = toscaTransformer.getNodeType(nodeOS.getValue().toString(), nodeOSRelease.getValue().toString());
            TNodeTemplate nodeTemplate = ModelUtilities.instantiateNodeTemplate(nodeType);

            TEntityTemplate.Properties properties = nodeTemplate.getProperties();
            if (properties != null && properties.getKVProperties() != null) {
                LinkedHashMap<String, String> kvProperties = properties.getKVProperties();
                kvProperties.put(Constants.VMIP, node.getFactByName("ipaddress").getValue().toString());
                kvProperties.put(Constants.VM_INSTANCE_ID, node.getCertname());
                kvProperties.put(Constants.VM_PRIVATE_KEY, this.master.getGeneratedPrivateKey());
                kvProperties.put(Constants.VM_PUBLIC_KEY, this.master.getGeneratedPublicKey());
                kvProperties.put(Constants.VM_USER_NAME, nodeOS.getValue().toString().toLowerCase());
                properties.setKVProperties(kvProperties);
            }

            topologyTemplate.addNodeTemplate(nodeTemplate);
        });
        TServiceTemplate serviceTemplate = new TServiceTemplate.Builder(this.master.getId(), topologyTemplate)
            .addTags(new TTags.Builder()
                .addTag("deploymentTechnology", PUPPET.getName())
                .build()
            ).build();
    }

    @Override
    public void transformEdmmiToTOSCA() {
        ServiceTemplateInstance serviceTemplateInstance = toscaTransformer.transformEDiMMToServiceTemplateInstance(deploymentInstance);
        OpenTOSCAConnector.processServiceTemplateInstanceToOpenTOSCA(context.getSourceTechnology().getName(), serviceTemplateInstance, context.getOutputPath() + deploymentInstance.getName() + ".csar");
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
