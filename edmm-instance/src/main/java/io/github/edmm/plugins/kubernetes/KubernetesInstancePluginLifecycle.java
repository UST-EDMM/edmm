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
import io.github.edmm.plugins.kubernetes.util.KubernetesDeploymentPropertiesHandler;
import io.github.edmm.plugins.kubernetes.util.KubernetesMetadataHandler;
import io.github.edmm.plugins.kubernetes.util.KubernetesPodsHandler;
import io.github.edmm.plugins.kubernetes.util.KubernetesStateHandler;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1Pod;

public class KubernetesInstancePluginLifecycle extends AbstractLifecycleInstancePlugin {

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
        AuthenticatorImpl authenticator = new AuthenticatorImpl(kubeConfigPath, inputDeploymentName);
        authenticator.authenticate();

        this.appsApi = authenticator.getAppsApi();
        this.coreV1Api = authenticator.getCoreV1Api();
    }

    @Override
    public void getModels() {
        ApiInteractorImpl apiInteractor = new ApiInteractorImpl(this.appsApi, this.coreV1Api, this.inputDeploymentName);
        this.kubernetesDeploymentInstance = apiInteractor.getDeployment();
        this.podsOfDeploymentInstance = apiInteractor.getComponents();
    }

    @Override
    public void transformToEDIMM() {
        this.deploymentInstance.setName(this.kubernetesDeploymentInstance.getMetadata().getName());
        this.deploymentInstance.setCreatedAt(String.valueOf(this.kubernetesDeploymentInstance.getMetadata().getCreationTimestamp()));
        this.deploymentInstance.setVersion(KubernetesConstants.VERSION + this.kubernetesDeploymentInstance.getMetadata().getAnnotations().get(KubernetesConstants.VERSION));
        this.deploymentInstance.setMetadata(new KubernetesMetadataHandler(this.kubernetesDeploymentInstance.getMetadata()).getMetadata());
        this.deploymentInstance.setId(this.kubernetesDeploymentInstance.getMetadata().getUid());
        this.deploymentInstance.setState(KubernetesStateHandler.getDeploymentInstanceState(this.kubernetesDeploymentInstance.getStatus()));
        this.deploymentInstance.setComponentInstances(KubernetesPodsHandler.getComponentInstances(this.podsOfDeploymentInstance));
        this.deploymentInstance.setInstanceProperties(new KubernetesDeploymentPropertiesHandler(this.kubernetesDeploymentInstance.getStatus()).getDeploymentInstanceProperties());
    }

    @Override
    public void transformToTOSCA() {
        TOSCATransformer toscaTransformer = new TOSCATransformer();
        ServiceTemplateInstance serviceTemplateInstance = toscaTransformer.transformEDiMMToServiceTemplateInstance(this.deploymentInstance);
    }

    @Override
    public void createYAML() {
        YamlTransformer yamlTransformer = new YamlTransformer();
        yamlTransformer.createYamlforEDiMM(this.deploymentInstance, context.getPath());
    }

    @Override
    public void cleanup() {
    }
}
