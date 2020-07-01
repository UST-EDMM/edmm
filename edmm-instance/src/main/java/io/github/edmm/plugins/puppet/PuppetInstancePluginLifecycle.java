package io.github.edmm.plugins.puppet;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.yaml.YamlTransformer;
import io.github.edmm.exporter.WineryExporter;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;
import io.github.edmm.plugins.puppet.api.ApiInteractorImpl;
import io.github.edmm.plugins.puppet.api.AuthenticatorImpl;
import io.github.edmm.plugins.puppet.model.Master;
import io.github.edmm.plugins.puppet.model.PuppetState;
import io.github.edmm.plugins.puppet.util.PuppetNodeHandler;

public class PuppetInstancePluginLifecycle extends AbstractLifecycleInstancePlugin {
    // puppet master info
    private String user = "ubuntu";
    private String ip = "master-ip";
    private String privateKeyLocation = "master-private-key";
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
        this.deploymentInstance.setId(this.master.getId());
        this.deploymentInstance.setCreatedAt(this.master.getCreatedAtTimestamp());
        this.deploymentInstance.setName(this.master.getHostName());
        this.deploymentInstance.setVersion(this.master.getPuppetVersion());
        this.deploymentInstance.setComponentInstances(PuppetNodeHandler.getComponentInstances(this.master));
        this.deploymentInstance.setState(PuppetState.getDeploymentInstanceState(this.master));
        // special case since master is deployment and component instance
        // TODO metadata, instance properties, description? if any
    }

    @Override
    public void transformToTOSCA() {
        TOSCATransformer toscaTransformer = new TOSCATransformer();
        ServiceTemplateInstance serviceTemplateInstance = toscaTransformer.transformEDiMMToServiceTemplateInstance(deploymentInstance);
        WineryExporter.exportServiceTemplateInstanceToWinery(serviceTemplateInstance, context.getPath() + deploymentInstance.getName() + ".csar");
        System.out.println("Transformed to OpenTOSCA Service Template Instance: " + serviceTemplateInstance.getCsarId());
    }

    @Override
    public void createYAML() {
        YamlTransformer yamlTransformer = new YamlTransformer();
        yamlTransformer.createYamlforEDiMM(this.deploymentInstance, context.getPath());
        System.out.println("Saved YAML for EDiMM to " + yamlTransformer.getFileOutputLocation());
    }

    @Override
    public void cleanup() {
        this.master.getSession().disconnect();
    }
}
