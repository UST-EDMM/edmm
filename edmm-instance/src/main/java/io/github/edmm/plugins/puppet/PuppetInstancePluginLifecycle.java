package io.github.edmm.plugins.puppet;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.plugins.puppet.api.ApiInteractorImpl;
import io.github.edmm.plugins.puppet.api.AuthenticatorImpl;

import com.jcraft.jsch.Session;

public class PuppetInstancePluginLifecycle extends AbstractLifecycleInstancePlugin {
    private Session session;

    PuppetInstancePluginLifecycle(InstanceTransformationContext context) {
        super(context);
    }

    @Override
    public void prepare() {
        AuthenticatorImpl authenticator = new AuthenticatorImpl();
        authenticator.authenticate();

        this.session = authenticator.getSession();
    }

    @Override
    public void getModels() {
        ApiInteractorImpl apiInteractor = new ApiInteractorImpl(this.session);
        apiInteractor.getDeployment();
    }

    @Override
    public void transformToEDIMM() {

    }

    @Override
    public void transformToTOSCA() {

    }

    @Override
    public void createYAML() {

    }

    @Override
    public void cleanup() {

    }
}
