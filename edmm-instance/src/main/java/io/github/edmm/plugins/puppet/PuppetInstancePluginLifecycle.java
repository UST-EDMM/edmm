package io.github.edmm.plugins.puppet;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.yaml.EDMMiYamlTransformer;
import io.github.edmm.exporter.WineryExporter;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;
import io.github.edmm.plugins.puppet.api.ApiInteractorImpl;
import io.github.edmm.plugins.puppet.api.AuthenticatorImpl;
import io.github.edmm.plugins.puppet.model.Master;
import io.github.edmm.plugins.puppet.model.PuppetState;
import io.github.edmm.plugins.puppet.util.PuppetNodeHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuppetInstancePluginLifecycle extends AbstractLifecycleInstancePlugin<PuppetInstancePluginLifecycle> {

    private final static Logger logger = LoggerFactory.getLogger(PuppetInstancePluginLifecycle.class);
    private static final SourceTechnology PUPPET = SourceTechnology.builder().id("puppet").name("Puppet").build();

    // puppet master info
    private final String user;
    private final String ip;
    private final String privateKeyLocation;
    private final Integer port;
    private final String operatingSystem;
    private final String operatingSystemRelease;

    private Master master;
    private DeploymentInstance deploymentInstance = new DeploymentInstance();

    public PuppetInstancePluginLifecycle(InstanceTransformationContext context, String user, String ip, String privateKeyLocation, Integer port, String operatingSystem, String operatingSystemRelease) {
        super(context);
        this.user = user;
        this.ip = ip;
        this.privateKeyLocation = privateKeyLocation;
        this.port = port;
        this.operatingSystem = operatingSystem;
        this.operatingSystemRelease = operatingSystemRelease;
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
    public void transformToEDMMi() {
        this.deploymentInstance.setId(this.master.getId());
        this.deploymentInstance.setCreatedAt(this.master.getCreatedAtTimestamp());
        this.deploymentInstance.setName(this.master.getHostName());
        this.deploymentInstance.setVersion(this.master.getPuppetVersion());
        this.deploymentInstance.setComponentInstances(PuppetNodeHandler.getComponentInstances(this.master));
        this.deploymentInstance.setState(PuppetState.getDeploymentInstanceState(this.master));
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
        this.master.getSession().disconnect();
    }
}
