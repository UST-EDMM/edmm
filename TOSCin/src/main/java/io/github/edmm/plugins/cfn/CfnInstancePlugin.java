package io.github.edmm.plugins.cfn;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.model.ToscaDeploymentTechnology;
import io.github.edmm.model.ToscaDiscoveryPlugin;
import io.github.edmm.plugins.cfn.api.ApiInteractorImpl;
import io.github.edmm.plugins.cfn.api.AuthenticatorImpl;
import io.github.edmm.plugins.cfn.model.Template;
import io.github.edmm.plugins.cfn.resourcehandlers.ResourceHandler;
import io.github.edmm.plugins.cfn.resourcehandlers.ec2.EC2InstanceHandler;
import io.github.edmm.plugins.cfn.resourcehandlers.ec2.KeyMapper;
import io.github.edmm.util.Constants;
import io.github.edmm.util.Util;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.StackResourceDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CfnInstancePlugin extends AbstractLifecycleInstancePlugin<CfnInstancePlugin> {
    private final static Logger logger = LoggerFactory.getLogger(CfnInstancePlugin.class);

    private final String inputStackName;
    private final String region;
    private final String profileName;
    private final TOSCATransformer toscaTransformer;
    private final ToscaDeploymentTechnology cfnTechnology;
    private final ToscaDiscoveryPlugin cfnDiscoveryPlugin;
    private List<ResourceHandler> resourceHandlers;
    private AmazonCloudFormation cloudFormation;
    private Stack stack;
    private Template template;
    private List<StackResourceDetail> stackResources;

    public CfnInstancePlugin(InstanceTransformationContext context,
                             String inputStackName,
                             String region,
                             String profileName) {
        super(context);
        this.inputStackName = Objects.requireNonNull(inputStackName);
        this.region = Objects.requireNonNull(region);
        this.profileName = profileName;
        toscaTransformer = new TOSCATransformer();

        cfnTechnology = new ToscaDeploymentTechnology();
        cfnTechnology.setId(getContext().getId());
        cfnTechnology.setSourceTechnology(getContext().getSourceTechnology());
        cfnTechnology.setManagedIds(Collections.emptyList());
        cfnTechnology.setProperties(Collections.emptyMap());

        cfnDiscoveryPlugin = new ToscaDiscoveryPlugin();
        cfnDiscoveryPlugin.setId(getContext().getId());
        cfnDiscoveryPlugin.setDiscoveredIds(Collections.emptyList());
        cfnDiscoveryPlugin.setSourceTechnology(getContext().getSourceTechnology());
    }

    @Override
    public void prepare() {
        AuthenticatorImpl authenticator = new AuthenticatorImpl(region, profileName);
        authenticator.authenticate();

        this.cloudFormation = authenticator.getCloudFormation();

        resourceHandlers = Arrays.asList(new EC2InstanceHandler(toscaTransformer,
            authenticator.getCredentialsProvider(),
            region,
            new KeyMapper(),
            cfnTechnology,
            cfnDiscoveryPlugin));

        ApiInteractorImpl interactor = new ApiInteractorImpl(this.cloudFormation, this.inputStackName);
        this.stack = interactor.getDeployment();
        this.stackResources = interactor.getComponents();
        this.template = interactor.getModel();
    }

    @Override
    public void transformToTOSCA() {
        TServiceTemplate serviceTemplate = Optional.ofNullable(retrieveGeneratedServiceTemplate()).orElseGet(() -> {
            String serviceTemplateId = "cfn-" + UUID.randomUUID();
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

        ObjectMapper objectMapper = new ObjectMapper();
        List<ToscaDeploymentTechnology> deploymentTechnologies = Util.extractDeploymentTechnologiesFromServiceTemplate(
            serviceTemplate,
            objectMapper);
        deploymentTechnologies.add(cfnTechnology);

        List<ToscaDiscoveryPlugin> toscaDiscoveryPlugins = Util.extractDiscoveryPluginsFromServiceTemplate(
            serviceTemplate,
            objectMapper);
        toscaDiscoveryPlugins.add(cfnDiscoveryPlugin);

        Map<String, String> cfnProperties = new HashMap<>();
        cfnProperties.put("Region", region);
        cfnProperties.put("StackName", inputStackName);

        cfnTechnology.setProperties(cfnProperties);

        stackResources.forEach(curResource -> resourceHandlers.stream()
            .filter(resourceHandler -> resourceHandler.canHandleResource(curResource.getResourceType()))
            .findFirst()
            .ifPresent(resourceHandler -> resourceHandler.addResourceToTemplate(serviceTemplate, curResource)));

        Util.updateDeploymenTechnologiesInServiceTemplate(serviceTemplate, objectMapper, deploymentTechnologies);
        Util.updateDiscoveryPluginsInServiceTemplate(serviceTemplate, objectMapper, toscaDiscoveryPlugins);

        updateGeneratedServiceTemplate(serviceTemplate);
    }

    @Override
    public void storeTransformedTOSCA() {

    }

    @Override
    public void cleanup() {
    }
}
