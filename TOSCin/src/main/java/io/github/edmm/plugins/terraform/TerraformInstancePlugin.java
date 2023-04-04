package io.github.edmm.plugins.terraform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.exporter.WineryConnector;
import io.github.edmm.model.DeploymentTechnologyDescriptor;
import io.github.edmm.model.DiscoveryPluginDescriptor;
import io.github.edmm.plugins.terraform.model.TerraformBackendInfo;
import io.github.edmm.plugins.terraform.model.TerraformState;
import io.github.edmm.plugins.terraform.resourcehandlers.ResourceHandler;
import io.github.edmm.plugins.terraform.resourcehandlers.ec2.EC2InstanceHandler;
import io.github.edmm.plugins.terraform.resourcehandlers.ec2.KeyMapper;
import io.github.edmm.plugins.terraform.typemapper.WindowsMapper;
import io.github.edmm.util.Constants;
import io.github.edmm.util.Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerraformInstancePlugin extends AbstractLifecycleInstancePlugin<TerraformInstancePlugin> {
    private final static Logger logger = LoggerFactory.getLogger(TerraformInstancePlugin.class);

    private final Path terraformStateFile;
    private final TOSCATransformer toscaTransformer;
    private final List<ResourceHandler> resourceHandlers;
    private final DeploymentTechnologyDescriptor terraformTechnology;
    private final DiscoveryPluginDescriptor terraformDiscoveryPlugin;

    private TerraformBackendInfo terraformBackendInfo;
    private TerraformState terraformState;

    public TerraformInstancePlugin(
        InstanceTransformationContext context, Path terraformStateFile) {
        super(context);
        this.terraformStateFile = terraformStateFile;
        String terraformNodeId = "terraform-backend-" + UUID.randomUUID();
        WineryConnector wineryConnector = WineryConnector.getInstance();
        toscaTransformer = new TOSCATransformer(Arrays.asList(new WindowsMapper(wineryConnector)));

        terraformTechnology = new DeploymentTechnologyDescriptor();
        terraformTechnology.setId(terraformNodeId);
        terraformTechnology.setTechnologyId(getContext().getSourceTechnology().getId());
        terraformTechnology.setManagedIds(Collections.emptyList());
        terraformTechnology.setProperties(Collections.emptyMap());

        terraformDiscoveryPlugin = new DiscoveryPluginDescriptor();
        terraformDiscoveryPlugin.setId(getContext().getSourceTechnology().getId());
        terraformDiscoveryPlugin.setDiscoveredIds(Collections.emptyList());

        resourceHandlers = Arrays.asList(new EC2InstanceHandler(toscaTransformer,
            terraformTechnology,
            terraformDiscoveryPlugin,
            new KeyMapper()));
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
    public void transformToTOSCA() {
        TServiceTemplate serviceTemplate = Optional.ofNullable(retrieveGeneratedServiceTemplate()).orElseGet(() -> {
            String serviceTemplateId = "terraform-" + UUID.randomUUID();
            logger.info("Creating new service template for transformation |{}|", serviceTemplateId);
            return new TServiceTemplate.Builder(serviceTemplateId, Constants.TOSCA_NAME_SPACE_RETRIEVED_INSTANCES, new TTopologyTemplate()).setName(serviceTemplateId)
                .addTag(new TTag.Builder("deploymentTechnology", getContext().getSourceTechnology().getName()).build())
                .build();
        });

        ObjectMapper objectMapper = new ObjectMapper();
        List<DeploymentTechnologyDescriptor> deploymentTechnologies = Util.extractDeploymentTechnologiesFromServiceTemplate(
            serviceTemplate,
            objectMapper);
        deploymentTechnologies.add(terraformTechnology);

        List<DiscoveryPluginDescriptor> discoveryPluginDescriptors = Util.extractDiscoveryPluginsFromServiceTemplate(
            serviceTemplate,
            objectMapper);
        discoveryPluginDescriptors.add(terraformDiscoveryPlugin);

        Map<String, String> terraformProperties = new HashMap<>();
        terraformProperties.put("Version", terraformBackendInfo.getTerraformVersion());

        terraformTechnology.setProperties(terraformProperties);

        terraformState.getResources()
            .forEach(curResource -> resourceHandlers.stream()
                .filter(resourceHandler -> resourceHandler.canHandleResource(curResource))
                .findFirst()
                .ifPresent(resourceHandler -> resourceHandler.addResourceToTemplate(serviceTemplate, curResource)));

        Util.updateDeploymentTechnologiesInServiceTemplate(serviceTemplate, objectMapper, deploymentTechnologies);
        Util.updateDiscoveryPluginsInServiceTemplate(serviceTemplate, objectMapper, discoveryPluginDescriptors);

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
}
