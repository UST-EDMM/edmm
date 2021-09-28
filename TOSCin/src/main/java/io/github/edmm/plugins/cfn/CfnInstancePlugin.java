package io.github.edmm.plugins.cfn;

import java.util.List;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.plugins.cfn.api.ApiInteractorImpl;
import io.github.edmm.plugins.cfn.api.AuthenticatorImpl;
import io.github.edmm.plugins.cfn.model.Template;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.StackResourceDetail;

public class CfnInstancePlugin extends AbstractLifecycleInstancePlugin<CfnInstancePlugin> {
    private AmazonCloudFormation cloudFormation;
    private Stack stack;
    private String inputStackName;
    private Template template;
    private List<StackResourceDetail> stackResources;

    public CfnInstancePlugin(InstanceTransformationContext context) {
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
    public void transformDirectlyToTOSCA() {

    }

    @Override
    public void storeTransformedTOSCA() {

    }

    @Override
    public void cleanup() {
    }
}
