package io.github.edmm.plugins.heat;

import java.util.List;
import java.util.Map;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.plugins.heat.api.ApiInteractorImpl;
import io.github.edmm.plugins.heat.api.AuthenticatorImpl;

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
    public void transformDirectlyToTOSCA() {

    }

    @Override
    public void storeTransformedTOSCA() {

    }

    @Override
    public void cleanup() {
    }
}
