package io.github.edmm.plugins.cfn;

import java.util.List;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.yaml.EDMMiYamlTransformer;
import io.github.edmm.exporter.WineryExporter;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;
import io.github.edmm.plugins.cfn.api.ApiInteractorImpl;
import io.github.edmm.plugins.cfn.api.AuthenticatorImpl;
import io.github.edmm.plugins.cfn.model.Status;
import io.github.edmm.plugins.cfn.model.Template;
import io.github.edmm.plugins.cfn.util.CfnMetadataHandler;
import io.github.edmm.plugins.cfn.util.CfnStackPropertiesHandler;
import io.github.edmm.plugins.cfn.util.CfnStackResourcesHandler;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.StackResourceDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CfnInstancePluginLifecycle extends AbstractLifecycleInstancePlugin<CfnInstancePluginLifecycle> {

    private static final Logger logger = LoggerFactory.getLogger(CfnInstancePluginLifecycle.class);

    private final DeploymentInstance deploymentInstance = new DeploymentInstance();
    private AmazonCloudFormation cloudFormation;
    private Stack stack;
    private String inputStackName;
    private Template template;
    private List<StackResourceDetail> stackResources;

    CfnInstancePluginLifecycle(InstanceTransformationContext context) {
        super(context);
    }

    @Override
    public void prepare() {
        this.inputStackName = context.getId();
        AuthenticatorImpl authenticator = new AuthenticatorImpl();
        authenticator.authenticate();

        this.cloudFormation = authenticator.getCloudFormation();
    }

    @Override
    public void getModels() {
        ApiInteractorImpl interactor = new ApiInteractorImpl(this.cloudFormation, this.inputStackName);
        this.stack = interactor.getDeployment();
        this.stackResources = interactor.getComponents();
        this.template = interactor.getModel();
    }

    @Override
    public void transformToEDMMi() {
        this.deploymentInstance.setName(this.stack.getStackName());
        this.deploymentInstance.setId(this.stack.getStackId());
        this.deploymentInstance.setVersion(this.template.getAWSTemplateFormatVersion());
        this.deploymentInstance.setCreatedAt(String.valueOf(this.stack.getCreationTime()));
        this.deploymentInstance.setDescription(this.stack.getDescription());
        this.deploymentInstance.setState(Status.CfnStackStatus.valueOf(this.stack.getStackStatus()).toEDiMMDeploymentInstanceState());
        this.deploymentInstance.setInstanceProperties(new CfnStackPropertiesHandler().getInstanceProperties(this.stack.getParameters(), this.stack.getOutputs()));
        this.deploymentInstance.setMetadata(new CfnMetadataHandler(this.stack).getMetadataForDeploymentInstance());
        this.deploymentInstance.setComponentInstances(new CfnStackResourcesHandler(this.stackResources, this.template).getComponentInstances());
    }

    @Override
    public void transformToTOSCA() {
        TOSCATransformer toscaTransformer = new TOSCATransformer();
        ServiceTemplateInstance serviceTemplateInstance = toscaTransformer.transformEDiMMToServiceTemplateInstance(deploymentInstance);
        WineryExporter.processServiceTemplateInstanceToOpenTOSCA(context.getSourceTechnology().getName(), serviceTemplateInstance, context.getOutputPath() + deploymentInstance.getName() + ".csar");
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
    }
}
