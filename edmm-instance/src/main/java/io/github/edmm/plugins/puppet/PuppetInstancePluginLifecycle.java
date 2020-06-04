package io.github.edmm.plugins.puppet;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.plugins.puppet.api.ApiInteractorImpl;
import io.github.edmm.plugins.puppet.api.AuthenticatorImpl;
import io.github.edmm.plugins.puppet.model.Master;
import io.github.edmm.plugins.puppet.util.PuppetNodeHandler;

public class PuppetInstancePluginLifecycle extends AbstractLifecycleInstancePlugin {
    // puppet master info
    private String user = "ubuntu";
    private String ip = "master-ip";
    private String privateKeyLocation = "master-private-key-location";
    private Integer port = 22;
    private String operatingSystem = "Ubuntu";
    private String operatingSystemRelease = "18.04";

    private Master master;
    private DeploymentInstance deploymentInstance = new DeploymentInstance();

    PuppetInstancePluginLifecycle(InstanceTransformationContext context) {
        super(context);
    }

    @Override
    public void prepare() {
        AuthenticatorImpl authenticator = new AuthenticatorImpl(new Master(this.user, this.ip, this.privateKeyLocation, this.port));
        authenticator.authenticate();
        this.master = authenticator.getMaster();
    }

    @Override
    public void getModels() {
        ApiInteractorImpl apiInteractor = new ApiInteractorImpl(this.master);
        this.master = apiInteractor.getDeployment();
        this.master.setOperatingSystem(this.operatingSystem);
        this.master.setOperatingSystemRelease(this.operatingSystemRelease);
    }

    @Override
    public void transformToEDIMM() {
        this.deploymentInstance.setId(String.valueOf((this.master.getHostName() + this.master.getIp()).hashCode()));
        this.deploymentInstance.setCreatedAt(this.master.getCreatedAtTimestamp());
        this.deploymentInstance.setName(this.master.getHostName());
        this.deploymentInstance.setVersion(this.master.getPuppetVersion());
        this.deploymentInstance.setComponentInstances(PuppetNodeHandler.getComponentInstances(this.master.getNodes()));
        // special case since master is deployment and component instance
        this.deploymentInstance.getComponentInstances().add(this.master.toComponentInstance());
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
