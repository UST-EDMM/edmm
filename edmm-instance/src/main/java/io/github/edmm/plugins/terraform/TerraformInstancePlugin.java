package io.github.edmm.plugins.terraform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.exporter.WineryConnector;
import io.github.edmm.plugins.terraform.model.TerraformBackendInfo;
import io.github.edmm.plugins.terraform.model.TerraformState;
import io.github.edmm.plugins.terraform.resourcehandlers.ResourceHandler;
import io.github.edmm.plugins.terraform.resourcehandlers.ec2.EC2InstanceHandler;
import io.github.edmm.plugins.terraform.typemapper.WindowsMapper;
import io.github.edmm.util.Constants;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerraformInstancePlugin extends AbstractLifecycleInstancePlugin<TerraformInstancePlugin> {
    private final static Logger logger = LoggerFactory.getLogger(TerraformInstancePlugin.class);

    private final Path terraformStateFile;
    private final TOSCATransformer toscaTransformer;
    private final WineryConnector wineryConnector;
    private final List<ResourceHandler> resourceHandlers;
    private final String terraformNodeId;

    private TerraformBackendInfo terraformBackendInfo;
    private TerraformState terraformState;

    public TerraformInstancePlugin(
        InstanceTransformationContext context, Path terraformStateFile) {
        super(context);
        this.terraformStateFile = terraformStateFile;
        terraformNodeId = "terraform-backend-" + UUID.randomUUID();
        wineryConnector = WineryConnector.getInstance();
        toscaTransformer = new TOSCATransformer(Arrays.asList(new WindowsMapper(wineryConnector)));
        resourceHandlers = Arrays.asList(new EC2InstanceHandler(toscaTransformer, terraformNodeId));
    }

    @Override
    public void prepare() {
        if (Files.notExists(terraformStateFile)) {
            throw new IllegalArgumentException("State file |" + terraformStateFile + "| does not exist");
        }
        if (!(Files.isRegularFile(terraformStateFile) && Files.isReadable(terraformStateFile))) {
            throw new IllegalArgumentException("Cannot read state file |" + terraformStateFile + "|");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        terraformState = parseStateFile(objectMapper);

        String version = terraformState.getTerraformVersion();
        terraformBackendInfo = new TerraformBackendInfo(SystemUtils.OS_NAME, SystemUtils.OS_VERSION, version);
    }

    @Override
    public void getModels() {
        // nothing to do, as resources have already been parsed with terraform state
    }

    @Override
    public void transformDirectlyToTOSCA() {
        TServiceTemplate serviceTemplate = Optional.ofNullable(retrieveGeneratedServiceTemplate()).orElseGet(() -> {
            String serviceTemplateId = "terraform-" + UUID.randomUUID();
            logger.info("Creating new service template for transformation |{}|", serviceTemplateId);
            TTopologyTemplate topologyTemplate = new TTopologyTemplate();
            return new TServiceTemplate.Builder(serviceTemplateId, topologyTemplate).setName(serviceTemplateId)
                .setTargetNamespace(Constants.TOSCA_NAME_SPACE_RETRIEVED_INSTANCES)
                .addTags(new TTags.Builder().addTag("deploymentTechnology",
                    getContext().getSourceTechnology().getName()).build())
                .build();
        });

        TTopologyTemplate topologyTemplate = Optional.ofNullable(serviceTemplate.getTopologyTemplate())
            .orElseGet(() -> {
                logger.info("Creating new topology template, as existing service template has none");
                TTopologyTemplate topologyTemplate1 = new TTopologyTemplate();
                serviceTemplate.setTopologyTemplate(topologyTemplate1);
                return topologyTemplate1;
            });

        TNodeType backendNodeType = toscaTransformer.getComputeNodeType(terraformBackendInfo.getOperatingSystem(),
            terraformBackendInfo.getOperatingSystemVersion());
        TNodeTemplate backendNode = ModelUtilities.instantiateNodeTemplate(backendNodeType);
        topologyTemplate.addNodeTemplate(backendNode);

        TNodeType terraformNodeType = toscaTransformer.getSoftwareNodeType("Terraform", null);
        TNodeTemplate terraformNode = ModelUtilities.instantiateNodeTemplate(terraformNodeType);
        terraformNode.setId(terraformNodeId);
        Map<String, String> terraformProperties = new HashMap<>();
        terraformProperties.put("Version", terraformBackendInfo.getTerraformVersion());

        populateNodeTemplateProperties(terraformNode, terraformProperties);

        topologyTemplate.addNodeTemplate(terraformNode);

        ModelUtilities.createRelationshipTemplateAndAddToTopology(terraformNode,
            backendNode,
            ToscaBaseTypes.hostedOnRelationshipType,
            topologyTemplate);

        terraformState.getResources()
            .forEach(curResource -> resourceHandlers.stream()
                .filter(resourceHandler -> resourceHandler.canHandleResource(curResource))
                .findFirst()
                .ifPresent(resourceHandler -> resourceHandler.addResourceToTemplate(serviceTemplate, curResource)));

        updateGeneratedServiceTemplate(serviceTemplate);
    }

    @Override
    public void storeTransformedTOSCA() {
        Optional.ofNullable(retrieveGeneratedServiceTemplate()).ifPresent(toscaTransformer::save);
    }

    @Override
    public void cleanup() {

    }

    private TerraformState parseStateFile(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(terraformStateFile.toFile(), TerraformState.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not parse terraform state", e);
        }
    }

    private void populateNodeTemplateProperties(TNodeTemplate nodeTemplate, Map<String, String> additionalProperties) {
        if (nodeTemplate.getProperties() != null && nodeTemplate.getProperties().getKVProperties() != null) {
            nodeTemplate.getProperties()
                .getKVProperties()
                .entrySet()
                .stream()
                .filter(entry -> !additionalProperties.containsKey(entry.getKey()) || additionalProperties.get(entry.getKey())
                    .isEmpty())
                .forEach(entry -> additionalProperties.put(entry.getKey(),
                    entry.getValue() != null && !entry.getValue()
                        .isEmpty() ? entry.getValue() : "get_input: " + entry.getKey() + "_" + nodeTemplate.getId()
                        .replaceAll("(\\s)|(:)|(\\.)", "_")));
        }

        // workaround to set new properties
        nodeTemplate.setProperties(new TEntityTemplate.Properties());
        nodeTemplate.getProperties().setKVProperties(additionalProperties);
    }
}
