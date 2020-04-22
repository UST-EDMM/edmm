package io.github.edmm.plugins.heat.api;

import java.util.List;
import java.util.Map;

import io.github.edmm.core.plugin.ApiInteractor;
import io.github.edmm.core.transformation.InstanceTransformationException;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.heat.Resource;
import org.openstack4j.model.heat.Stack;

public class ApiInteractorImpl implements ApiInteractor {

    private final OSClient.OSClientV3 osClient;
    private final String stackName;
    private final String stackId;

    public ApiInteractorImpl(OSClient.OSClientV3 osClient, String stackName, String stackId) {
        this.osClient = osClient;
        this.stackName = stackName;
        this.stackId = stackId;
    }

    @Override
    public Stack getDeployment() {
        Stack stack = this.osClient.heat().stacks().getDetails(this.stackName, this.stackId);
        if (stack == null) {
            throw new InstanceTransformationException("Unable to retrieve stack.");
        }
        return stack;
    }

    @Override
    public List<? extends Resource> getComponents() {
        List<? extends Resource> resources = this.osClient.heat().resources().list(this.stackName, this.stackId);
        if (resources == null) {
            throw new InstanceTransformationException("Stack does not have any resources! Exiting...");
        }
        return resources;
    }

    @Override
    public Map<String, Object> getModel() {
        Map<String, Object> template = this.osClient.heat().templates().getTemplateAsMap(this.stackName, this.stackId);
        if (template == null) {
            throw new InstanceTransformationException("Template could nto be found! Exiting...");
        }
        return template;
    }
}
