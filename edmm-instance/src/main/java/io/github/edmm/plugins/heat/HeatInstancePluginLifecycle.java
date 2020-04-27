package io.github.edmm.plugins.heat;

import java.util.List;
import java.util.Map;

import io.github.edmm.core.parser.YamlTransformer;
import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.core.transformation.TOSCATransformer;
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

public class HeatInstancePluginLifecycle extends AbstractLifecycleInstancePlugin {

    private static final String userName = "-";
    private static final String password = "-";
    private static final String projectId = "-";
    private static final String domainName = "-";
    private static final String authenticationEndpoint = "-";

    private static final String stackName = "-";
    private static final String stackId = "-";

    private Stack stack;
    private Map<String, Object> template;
    private OSClientV3 osClient;
    private List<? extends Resource> resources;
    private final DeploymentInstance deploymentInstance = new DeploymentInstance();

    HeatInstancePluginLifecycle(InstanceTransformationContext context) {
        super(context);
    }

    @Override
    public void prepare() {
        AuthenticatorImpl authenticator = new AuthenticatorImpl(authenticationEndpoint, userName, password, domainName, projectId);
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
    public void transformToEDIMM() {
        this.deploymentInstance.setId(this.stack.getId());
        this.deploymentInstance.setCreatedAt(this.stack.getCreationTime());
        this.deploymentInstance.setDescription(this.stack.getDescription());
        this.deploymentInstance.setName(this.stack.getName());
        this.deploymentInstance.setState(StackStatus.StackStatusForDeploymentInstance.valueOf(this.stack.getStatus()).toEDIMMDeploymentInstanceState());
        this.deploymentInstance.setVersion(String.valueOf(this.template.get(HeatConstants.VERSION)));
        this.deploymentInstance.setInstanceProperties(HeatPropertiesHandler.getDeploymentInstanceProperties(this.stack.getParameters(), this.stack.getOutputs()));
        this.deploymentInstance.setMetadata(HeatMetadataHandler.getDeploymentMetadata(this.stack.getTags(), this.stack.getTimeoutMins(), this.stack.getUpdatedTime()));
        // TODO move this
        this.resources.forEach(resource -> {
            Map<String, Map<String, Object>> resourceContent = (Map<String, Map<String, Object>>) this.template.get(HeatConstants.RESOURCES);
            this.deploymentInstance.addToComponentInstances(HeatResourceHandler.getComponentInstance(this.resources, resource, resourceContent));
        });
    }

    @Override
    public void transformToTOSCA() {
        TOSCATransformer toscaTransformer = new TOSCATransformer();
        ServiceTemplateInstance serviceTemplateInstance = toscaTransformer.transformEDiMMToServiceTemplateInstance(this.deploymentInstance);
        System.out.println("Transformed to OpenTOSCA Service Template Instance: " + serviceTemplateInstance.toString());
    }

    @Override
    public void createYAML() {
        YamlTransformer yamlTransformer = new YamlTransformer();
        yamlTransformer.createYamlforEDiMM(this.deploymentInstance, context.getPath());
        System.out.println("Saved YAML for EDiMM to " + yamlTransformer.getFileOutputLocation());
    }

    @Override
    public void cleanup() {
    }
}
