package io.github.edmm.plugins.kubernetes;

import java.util.List;

import io.github.edmm.core.parser.YamlTransformer;
import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;
import io.github.edmm.plugins.kubernetes.api.ApiInteractorImpl;
import io.github.edmm.plugins.kubernetes.api.AuthenticatorImpl;
import io.github.edmm.plugins.kubernetes.util.KubernetesConstants;
import io.github.edmm.plugins.kubernetes.util.Util;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1Pod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesInstancePluginLifecycle extends AbstractLifecycleInstancePlugin {
    private static final Logger logger = LoggerFactory.getLogger(KubernetesInstancePluginLifecycle.class);

    private final DeploymentInstance deploymentInstance = new DeploymentInstance();

    private AppsV1Api appsApi;
    private CoreV1Api coreV1Api;

    private V1Deployment kubernetesDeploymentInstance;
    private List<V1Pod> podsOfDeploymentInstance;

    // hardcoded for testing purposes
    private final String inputDeploymentName = "nginx-deployment";
    private final String kubeConfigPath = "/Users/tobi/.kube/config";

    KubernetesInstancePluginLifecycle(InstanceTransformationContext context) {
        super(context);
    }

    @Override
    public void prepare() {
        logger.info("Start preparing...");

        AuthenticatorImpl authenticator = new AuthenticatorImpl(kubeConfigPath, inputDeploymentName);
        authenticator.authenticate();

        this.appsApi = authenticator.getAppsApi();
        this.coreV1Api = authenticator.getCoreV1Api();

        logger.info("Finished preparing...");
    }

    @Override
    public void getModels() {
        logger.info("Start getting models...");

        ApiInteractorImpl apiInteractor = new ApiInteractorImpl(this.appsApi, this.coreV1Api, this.inputDeploymentName);
        this.kubernetesDeploymentInstance = apiInteractor.getDeployment();
        this.podsOfDeploymentInstance = apiInteractor.getComponents();

        logger.info("Finished getting models...");
    }

    @Override
    public void transformToEDIMM() {
        logger.info("Start transforming to EDiMM...");

        this.deploymentInstance.setName(this.kubernetesDeploymentInstance.getMetadata().getName());
        this.deploymentInstance.setCreatedAt(String.valueOf(this.kubernetesDeploymentInstance.getMetadata().getCreationTimestamp()));
        this.deploymentInstance.setVersion(KubernetesConstants.VERSION + this.kubernetesDeploymentInstance.getMetadata().getAnnotations().get(KubernetesConstants.VERSION));
        this.deploymentInstance.setMetadata(Util.getMetadata(this.kubernetesDeploymentInstance.getMetadata()));
        this.deploymentInstance.setId(this.kubernetesDeploymentInstance.getMetadata().getUid());
        this.deploymentInstance.setState(Util.getDeploymentInstanceState(this.kubernetesDeploymentInstance.getStatus()));
        this.deploymentInstance.setComponentInstances(Util.getComponentInstances(this.podsOfDeploymentInstance));
        this.deploymentInstance.setInstanceProperties(Util.getDeploymentInstanceProperties(this.kubernetesDeploymentInstance.getStatus()));

        logger.info("Finished transforming to EDiMM...");
    }

    @Override
    public void transformToTOSCA() {
        logger.info("Start transforming EDiMM to TOSCA...");

        TOSCATransformer toscaTransformer = new TOSCATransformer();
        ServiceTemplateInstance serviceTemplateInstance = TOSCATransformer.transformEDiMMToOpenTOSCA(this.deploymentInstance);

        logger.info("Finished transforming EDiMM to TOSCA...");
    }

    @Override
    public void createYAML() {
        logger.info("Start creating YAML for EDiMM...");

        YamlTransformer yamlTransformer = new YamlTransformer();
        String fileLocation = yamlTransformer.createYamlforEDiMM(this.deploymentInstance, context.getPath());

        logger.info("Finished creating YAML of EDiMM, saved to {}", fileLocation);
    }

    @Override
    public void cleanup() {
        logger.info("Skipping cleanup...");
    }
}
