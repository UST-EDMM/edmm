package io.github.edmm.plugins.cfn.api;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.core.plugin.ApiInteractor;
import io.github.edmm.plugins.cfn.model.Template;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.model.DescribeStackResourceRequest;
import com.amazonaws.services.cloudformation.model.DescribeStackResourcesRequest;
import com.amazonaws.services.cloudformation.model.GetTemplateRequest;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.StackResourceDetail;

public class ApiInteractorImpl implements ApiInteractor {

    private AmazonCloudFormation cloudFormation;
    private String inputStackName;

    public ApiInteractorImpl(AmazonCloudFormation cloudFormation, String inputStackName) {
        this.cloudFormation = cloudFormation;
        this.inputStackName = inputStackName;
    }

    @Override
    public Stack getDeployment() {
        return this.cloudFormation.describeStacks().getStacks().stream().filter(stack -> stack.getStackName().equals(this.inputStackName)).findFirst().orElse(null);
    }

    @Override
    public List<StackResourceDetail> getComponents() {
        List<StackResourceDetail> stackResources = new ArrayList<>();
        this.cloudFormation.describeStackResources(new DescribeStackResourcesRequest().withStackName(this.inputStackName)).getStackResources().forEach(
            stackResource -> stackResources.add(getDetailedComponent(this.inputStackName, stackResource.getLogicalResourceId())));
        return stackResources;
    }

    private StackResourceDetail getDetailedComponent(String inputStackName, String logicalResourceId) {
        return this.cloudFormation.describeStackResource(new DescribeStackResourceRequest().withStackName(inputStackName).withLogicalResourceId(logicalResourceId)).getStackResourceDetail();
    }

    @Override
    public Template getModel() {
        return Template.fromTemplateBodyString(this.cloudFormation.getTemplate(new GetTemplateRequest().withStackName(this.inputStackName)).getTemplateBody());
    }
}
