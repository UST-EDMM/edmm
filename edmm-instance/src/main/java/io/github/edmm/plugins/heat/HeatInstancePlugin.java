package io.github.edmm.plugins.heat;

import java.util.List;
import java.util.Map;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.yaml.EDMMiYamlTransformer;
import io.github.edmm.exporter.OpenTOSCAConnector;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;
import io.github.edmm.plugins.heat.api.ApiInteractorImpl;
import io.github.edmm.plugins.heat.api.AuthenticatorImpl;
import io.github.edmm.plugins.heat.model.StackStatus;
import io.github.edmm.plugins.heat.util.HeatConstants;
import io.github.edmm.plugins.heat.util.HeatMetadataHandler;
import io.github.edmm.plugins.heat.util.HeatPropertiesHandler;
import io.github.edmm.plugins.heat.util.HeatResourceHandler;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.exceptions.AuthenticationException;
import org.openstack4j.model.heat.Resource;
import org.openstack4j.model.heat.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeatInstancePlugin extends AbstractLifecycleInstancePlugin<HeatInstancePlugin> {

    private static final Logger logger = LoggerFactory.getLogger(HeatInstancePlugin.class);

    private final String userName;
    private final String password;
    private final String projectId;
    private final String domainName;
    private final String authenticationEndpoint;
    private final String stackName;
    private final String stackId;

    private final DeploymentInstance deploymentInstance = new DeploymentInstance();
    private Stack stack;
    private Map<String, Object> template;
    private OSClientV3 osClient;
    private List<? extends Resource> resources;

    public HeatInstancePlugin(
        InstanceTransformationContext context,
        String userName,
        String password,
        String projectId,
        String domainName,
        String authenticationEndpoint,
        String stackName,
        String stackId) {
        super(context);
        this.userName = userName;
        this.password = password;
        this.projectId = projectId;
        this.domainName = domainName;
        this.authenticationEndpoint = authenticationEndpoint;
        this.stackName = stackName;
        this.stackId = stackId;
    }

    @Override
    public void prepare() {
        AuthenticatorImpl authenticator = new AuthenticatorImpl(authenticationEndpoint,
            userName,
            password,
            domainName,
            projectId);
        try {
            authenticator.authenticate();
            this.osClient = authenticator.getHeatClient();
        } catch (AuthenticationException e) {
            throw new InstanceTransformationException("Failed to authenticate with OpenStack HEAT API", e.getCause());
        }
    }

    @Override
    public void getModels() {
        ApiInteractorImpl apiInteractor = new ApiInteractorImpl(this.osClient, stackName, stackId);
        this.stack = apiInteractor.getDeployment();
        this.template = apiInteractor.getModel();
        this.resources = apiInteractor.getComponents();
    }

    @Override
    public void transformToEDMMi() {
        // mapping complete
        this.deploymentInstance.setId(this.stack.getId());
        this.deploymentInstance.setCreatedAt(this.stack.getCreationTime());
        this.deploymentInstance.setDescription(this.stack.getDescription());
        this.deploymentInstance.setName(this.stack.getName());
        this.deploymentInstance.setState(StackStatus.StackStatusForDeploymentInstance.valueOf(this.stack.getStatus())
            .toEDIMMDeploymentInstanceState());
        this.deploymentInstance.setVersion(String.valueOf(this.template.get(HeatConstants.VERSION)));
        this.deploymentInstance.setInstanceProperties(HeatPropertiesHandler.getDeploymentInstanceProperties(this.stack.getParameters(),
            this.stack.getOutputs()));
        this.deploymentInstance.setMetadata(HeatMetadataHandler.getDeploymentMetadata(this.stack.getTags(),
            this.stack.getTimeoutMins(),
            this.stack.getUpdatedTime()));
        this.deploymentInstance.setComponentInstances(HeatResourceHandler.getComponentInstances(this.resources,
            this.template,
            this.osClient));
    }

    @Override
    public void transformEdmmiToTOSCA() {
        TOSCATransformer toscaTransformer = new TOSCATransformer();
        ServiceTemplateInstance serviceTemplateInstance = toscaTransformer.transformEDiMMToServiceTemplateInstance(
            deploymentInstance);
        OpenTOSCAConnector.processServiceTemplateInstanceToOpenTOSCA(context.getSourceTechnology().getName(),
            serviceTemplateInstance,
            context.getOutputPath() + deploymentInstance.getName() + ".csar");
        logger.info("Transformed to OpenTOSCA Service Template Instance: {}", serviceTemplateInstance.getCsarId());
    }

    @Override
    public void transformDirectlyToTOSCA() {

    }

    @Override
    public void storeTransformedTOSCA() {

    }

    @Override
    public void createYAML() {
        EDMMiYamlTransformer EDMMiYamlTransformer = new EDMMiYamlTransformer();
        EDMMiYamlTransformer.createYamlforEDiMM(this.deploymentInstance, context.getOutputPath());
        logger.info("Saved YAML for EDMMi to {}", EDMMiYamlTransformer.getFileOutputLocation());
    }

    @Override
    public void cleanup() {
    }
}
