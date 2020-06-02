package io.github.edmm.plugins.puppet;

import java.util.List;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.plugins.puppet.api.ApiInteractorImpl;
import io.github.edmm.plugins.puppet.api.AuthenticatorImpl;
import io.github.edmm.plugins.puppet.model.Master;
import io.github.edmm.plugins.puppet.model.Node;

public class PuppetInstancePluginLifecycle extends AbstractLifecycleInstancePlugin {
    private String user = "ubuntu";
    private String ip = "master-ip";
    private String privateKeyLocation = "master-private-key-location";
    private Integer port = 22;

    private Master master;
    private List<Node> nodes;
    private DeploymentInstance deploymentInstance;

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
        this.nodes = apiInteractor.getComponents();
        this.nodes.forEach(node -> node.setFacts(this.master.getFactsForNodeByCertName(node.getCertname())));
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
