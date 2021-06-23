package io.github.edmm.plugins.kubernetes;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.transformation.TypeTransformer;
import io.github.edmm.core.yaml.EDMMiYamlTransformer;
import io.github.edmm.exporter.OpenTOSCAConnector;
import io.github.edmm.exporter.WineryConnector;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;
import io.github.edmm.plugins.kubernetes.api.ApiInteractorImpl;
import io.github.edmm.plugins.kubernetes.api.AuthenticatorImpl;
import io.github.edmm.plugins.kubernetes.typemapper.UbuntuMapper;
import io.github.edmm.plugins.kubernetes.util.KubernetesConstants;
import io.github.edmm.plugins.kubernetes.util.KubernetesDeploymentPropertiesHandler;
import io.github.edmm.plugins.kubernetes.util.KubernetesMetadataHandler;
import io.github.edmm.plugins.kubernetes.util.KubernetesPodsHandler;
import io.github.edmm.plugins.kubernetes.util.KubernetesStateHandler;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Container;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1NodeList;
import io.kubernetes.client.models.V1NodeSystemInfo;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesInstancePlugin extends AbstractLifecycleInstancePlugin<KubernetesInstancePlugin> {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesInstancePlugin.class);
    private static final SourceTechnology KUBERNETES = SourceTechnology.builder().id("kubernetes").name("Kubernetes").build();
    private final DeploymentInstance deploymentInstance = new DeploymentInstance();

    private final String kubeConfigPath;
    private final WineryConnector myWineryConnector;
    private final TOSCATransformer myTOSCATransformer;
    private final List<TypeTransformer> myHostTransformers;
    private AppsV1Api appsApi;
    private CoreV1Api coreV1Api;
    private V1Deployment kubernetesDeploymentInstance;
    private List<V1Pod> podsOfDeploymentInstance;
    private String inputDeploymentName;

    public KubernetesInstancePlugin(InstanceTransformationContext context, String kubeConfigPath, String inputDeploymentName) {
        super(context);
        this.kubeConfigPath = kubeConfigPath;
        this.inputDeploymentName = inputDeploymentName;
        this.myWineryConnector = WineryConnector.getInstance();
        myHostTransformers = Arrays.asList(new UbuntuMapper(myWineryConnector));
        myTOSCATransformer = new TOSCATransformer(Arrays.asList(new UbuntuMapper(myWineryConnector)));
    }

    @Override
    public void prepare() {
        AuthenticatorImpl authenticator = new AuthenticatorImpl(kubeConfigPath);
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
        TTopologyTemplate topologyTemplate = new TTopologyTemplate();
        try {
            V1NodeList v1NodeList = this.coreV1Api.listNode(false, null, null, null, null, null, null, null, null);
            v1NodeList.getItems().stream().findFirst().ifPresent(aV1Node -> {
                V1NodeSystemInfo nodeInfo = aV1Node.getStatus().getNodeInfo();
                String osImage = nodeInfo.getOsImage();
                TNodeType hostNodeType = myHostTransformers.stream()
                    .filter(aTypeTransformer -> aTypeTransformer.canHandle(osImage, ""))
                    .findFirst()
                    .map(aTypeTransformer -> aTypeTransformer.performTransformation(osImage, ""))
                    .map(myWineryConnector::getNodeType)
                    .orElseGet(() -> myTOSCATransformer.getComputeNodeType(osImage, ""));
                TNodeTemplate hostTemplate = ModelUtilities.instantiateNodeTemplate(hostNodeType);
                hostTemplate.setId(nodeInfo.getMachineID());
                hostTemplate.setName(nodeInfo.getMachineID());
                topologyTemplate.addNodeTemplate(hostTemplate);

                String containerRuntimeVersion = nodeInfo.getContainerRuntimeVersion();
                int versionIdx = containerRuntimeVersion.indexOf("://");
                String containerRuntime = containerRuntimeVersion.substring(0, versionIdx);
                if (Objects.equals(containerRuntime, "docker")) {
                    TNodeType dockerEngineType = myTOSCATransformer.getComputeNodeType("DockerEngine", "");
                    TNodeTemplate dockerEngineTemplate = ModelUtilities.instantiateNodeTemplate(dockerEngineType);
                    dockerEngineTemplate.setName("KubeDockerEngine");
                    topologyTemplate.addNodeTemplate(dockerEngineTemplate);
                    ModelUtilities.createRelationshipTemplateAndAddToTopology(dockerEngineTemplate, hostTemplate,
                        ToscaBaseTypes.hostedOnRelationshipType, topologyTemplate);

                    try {
                        List<V1Container> containers;
                        if (this.inputDeploymentName != null) {
                            containers = this.kubernetesDeploymentInstance.getSpec().getTemplate().getSpec().getContainers();
                        } else {
                            V1PodList v1PodList = this.coreV1Api.listPodForAllNamespaces(null, null,
                                null, null, null, null,
                                null, null, null);
                            containers = v1PodList.getItems().stream().flatMap(aV1Pod -> aV1Pod.getSpec().getContainers().stream()).collect(Collectors.toList());
                        }

                        containers.forEach(aV1Container -> {
                            String image = aV1Container.getImage();
                            String name = aV1Container.getName();
                            TNodeType dockerContainerType = myTOSCATransformer.getComputeNodeType("DockerContainer", "");
                            TNodeTemplate dockerContainerTemplate = ModelUtilities.instantiateNodeTemplate(dockerContainerType);
                            dockerContainerTemplate.setName(name);
                            LinkedHashMap<String, String> kvProperties = Optional.ofNullable(dockerContainerTemplate.getProperties()).map(TEntityTemplate.Properties::getKVProperties).orElseGet(LinkedHashMap::new);
                            kvProperties.put("ContainerID", name);
                            kvProperties.put("ImageID", image);
                            TEntityTemplate.Properties properties = Optional.ofNullable(dockerContainerTemplate.getProperties()).orElseGet(TEntityTemplate.Properties::new);
                            properties.setKVProperties(kvProperties);
                            dockerContainerTemplate.setProperties(properties);
                            topologyTemplate.addNodeTemplate(dockerContainerTemplate);
                            ModelUtilities.createRelationshipTemplateAndAddToTopology(dockerContainerTemplate, dockerEngineTemplate, ToscaBaseTypes.hostedOnRelationshipType, topologyTemplate);
                        });
                    } catch (ApiException aE) {
                        this.logger.error("Error retrieving Pods", aE);
                    }
                }
            });
        } catch (ApiException aE) {
            this.logger.error("Error retrieving node list", aE);
        }

        TServiceTemplate serviceTemplate = new TServiceTemplate.Builder("kubernetes-" + this.kubernetesDeploymentInstance.getMetadata().getName(), topologyTemplate)
            .setName("kubernetes-" + this.kubernetesDeploymentInstance.getMetadata().getName())
            .setTargetNamespace("http://opentosca.org/retrieved/instances")
            .addTags(new TTags.Builder()
                .addTag("deploymentTechnology", KUBERNETES.getName())
                .build()
            ).build();

        updateGeneratedServiceTemplate(serviceTemplate);
    }

    @Override
    public void storeTransformedTOSCA() {
        Optional.ofNullable(retrieveGeneratedServiceTemplate()).ifPresent(myTOSCATransformer::save);
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
