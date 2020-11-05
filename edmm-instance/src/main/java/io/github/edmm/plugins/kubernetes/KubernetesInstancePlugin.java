package io.github.edmm.plugins.kubernetes;

import java.util.List;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.yaml.EDMMiYamlTransformer;
import io.github.edmm.exporter.OpenTOSCAConnector;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesInstancePlugin extends AbstractLifecycleInstancePlugin<KubernetesInstancePlugin> {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesInstancePlugin.class);
    private final DeploymentInstance deploymentInstance = new DeploymentInstance();

    private final String kubeConfigPath;
    private AppsV1Api appsApi;
    private CoreV1Api coreV1Api;
    private V1Deployment kubernetesDeploymentInstance;
    private List<V1Pod> podsOfDeploymentInstance;
    private String inputDeploymentName;

    public KubernetesInstancePlugin(InstanceTransformationContext context, String kubeConfigPath) {
        super(context);
        this.kubeConfigPath = kubeConfigPath;
    }

    @Override
    public void prepare() {
        this.inputDeploymentName = context.getId();
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
    public void transformToEDMMi() {
        this.deploymentInstance.setName(this.kubernetesDeploymentInstance.getMetadata().getName());
        this.deploymentInstance.setCreatedAt(String.valueOf(this.kubernetesDeploymentInstance.getMetadata().getCreationTimestamp()));
        this.deploymentInstance.setVersion(KubernetesConstants.VERSION + this.kubernetesDeploymentInstance.getMetadata().getAnnotations().get(KubernetesConstants.VERSION));
        this.deploymentInstance.setMetadata(new KubernetesMetadataHandler(this.kubernetesDeploymentInstance.getMetadata()).getMetadata(this.kubernetesDeploymentInstance.getApiVersion(), this.kubernetesDeploymentInstance.getKind()));
        this.deploymentInstance.setId(this.kubernetesDeploymentInstance.getMetadata().getUid());
        this.deploymentInstance.setState(KubernetesStateHandler.getDeploymentInstanceState(this.kubernetesDeploymentInstance.getStatus()));
        this.deploymentInstance.setComponentInstances(KubernetesPodsHandler.getComponentInstances(this.podsOfDeploymentInstance));
        this.deploymentInstance.setInstanceProperties(new KubernetesDeploymentPropertiesHandler(this.kubernetesDeploymentInstance.getStatus()).getDeploymentInstanceProperties());
    }

    @Override
    public void transformDirectlyToTOSCA() {

    }

    @Override
    public void transformEdmmiToTOSCA() {
        TOSCATransformer toscaTransformer = new TOSCATransformer();
        ServiceTemplateInstance serviceTemplateInstance = toscaTransformer.transformEDiMMToServiceTemplateInstance(this.deploymentInstance);
        OpenTOSCAConnector.processServiceTemplateInstanceToOpenTOSCA(context.getSourceTechnology().getName(), serviceTemplateInstance, context.getOutputPath() + deploymentInstance.getName() + ".csar");
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
    }
}
