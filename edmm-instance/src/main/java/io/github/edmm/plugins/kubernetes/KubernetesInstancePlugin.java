package io.github.edmm.plugins.kubernetes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import io.github.edmm.exporter.WineryConnector;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.plugins.kubernetes.api.ApiInteractorImpl;
import io.github.edmm.plugins.kubernetes.api.AuthenticatorImpl;
import io.github.edmm.plugins.kubernetes.typemapper.UbuntuMapper;
import io.github.edmm.util.Constants;

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
    private static final SourceTechnology KUBERNETES = SourceTechnology.builder()
        .id("kubernetes")
        .name("Kubernetes")
        .build();
    private final DeploymentInstance deploymentInstance = new DeploymentInstance();

    private final String kubeConfigPath;
    private final WineryConnector myWineryConnector;
    private final TOSCATransformer toscaTransformer;
    private final List<TypeTransformer> myHostTransformers;
    private final String inputDeploymentName;
    private AppsV1Api appsApi;
    private CoreV1Api coreV1Api;
    private V1Deployment kubernetesDeploymentInstance;
    private List<V1Pod> podsOfDeploymentInstance;

    public KubernetesInstancePlugin(
        InstanceTransformationContext context, String kubeConfigPath, String inputDeploymentName) {
        super(context);
        this.kubeConfigPath = kubeConfigPath;
        this.inputDeploymentName = inputDeploymentName;
        this.myWineryConnector = WineryConnector.getInstance();
        myHostTransformers = Arrays.asList(new UbuntuMapper(myWineryConnector));
        toscaTransformer = new TOSCATransformer(Arrays.asList(new UbuntuMapper(myWineryConnector)));
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
    public void transformDirectlyToTOSCA() {
        TServiceTemplate serviceTemplate = Optional.ofNullable(retrieveGeneratedServiceTemplate()).orElseGet(() -> {
            TTopologyTemplate topologyTemplate = new TTopologyTemplate();
            return new TServiceTemplate.Builder("kubernetes-" + this.kubernetesDeploymentInstance.getMetadata()
                .getName(), topologyTemplate).setName("kubernetes-" + this.kubernetesDeploymentInstance.getMetadata()
                .getName())
                .setTargetNamespace("http://opentosca.org/retrieved/instances")
                .addTags(new TTags.Builder().addTag("deploymentTechnology", KUBERNETES.getName()).build())
                .build();
        });

        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();

        TNodeType kubernetesNodeType = toscaTransformer.getSoftwareNodeType("Kubernetes", null);
        TNodeTemplate kubernetesCluster = ModelUtilities.instantiateNodeTemplate(kubernetesNodeType);
        String basePath = this.coreV1Api.getApiClient().getBasePath();
        Map<String, String> clusterProperties = new HashMap<>();
        clusterProperties.put(Constants.KUBERNETES_CLUSTER_IP, basePath);
        populateNodeTemplateProperties(kubernetesCluster, clusterProperties);
        topologyTemplate.addNodeTemplate(kubernetesCluster);

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
                    .orElseGet(() -> toscaTransformer.getComputeNodeType(osImage, ""));
                TNodeTemplate hostTemplate = ModelUtilities.instantiateNodeTemplate(hostNodeType);
                hostTemplate.setId(nodeInfo.getMachineID());
                hostTemplate.setName(nodeInfo.getMachineID());
                topologyTemplate.addNodeTemplate(hostTemplate);

                String containerRuntimeVersion = nodeInfo.getContainerRuntimeVersion();
                int versionIdx = containerRuntimeVersion.indexOf("://");
                String containerRuntime = containerRuntimeVersion.substring(0, versionIdx);
                if (Objects.equals(containerRuntime, "docker")) {
                    TNodeType dockerEngineType = toscaTransformer.getComputeNodeType("DockerEngine", "");
                    TNodeTemplate dockerEngineTemplate = ModelUtilities.instantiateNodeTemplate(dockerEngineType);
                    dockerEngineTemplate.setName("KubeDockerEngine");
                    topologyTemplate.addNodeTemplate(dockerEngineTemplate);
                    ModelUtilities.createRelationshipTemplateAndAddToTopology(dockerEngineTemplate,
                        hostTemplate,
                        ToscaBaseTypes.hostedOnRelationshipType,
                        topologyTemplate);
                    ModelUtilities.createRelationshipTemplateAndAddToTopology(dockerEngineTemplate,
                        kubernetesCluster,
                        Constants.deployedByRelationshipType,
                        topologyTemplate);

                    try {
                        List<V1Container> containers;
                        if (this.inputDeploymentName != null) {
                            containers = this.kubernetesDeploymentInstance.getSpec()
                                .getTemplate()
                                .getSpec()
                                .getContainers();
                        } else {
                            V1PodList v1PodList = this.coreV1Api.listPodForAllNamespaces(null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null);
                            containers = v1PodList.getItems()
                                .stream()
                                .flatMap(aV1Pod -> aV1Pod.getSpec().getContainers().stream())
                                .collect(Collectors.toList());
                        }

                        containers.forEach(aV1Container -> {
                            String image = aV1Container.getImage();
                            String name = aV1Container.getName();
                            TNodeType dockerContainerType = toscaTransformer.getComputeNodeType("DockerContainer", "");
                            TNodeTemplate dockerContainerTemplate = ModelUtilities.instantiateNodeTemplate(
                                dockerContainerType);
                            dockerContainerTemplate.setName(name);
                            LinkedHashMap<String, String> kvProperties = Optional.ofNullable(dockerContainerTemplate.getProperties())
                                .map(TEntityTemplate.Properties::getKVProperties)
                                .orElseGet(LinkedHashMap::new);
                            kvProperties.put("ContainerID", name);
                            kvProperties.put("ImageID", image);
                            TEntityTemplate.Properties properties = Optional.ofNullable(dockerContainerTemplate.getProperties())
                                .orElseGet(TEntityTemplate.Properties::new);
                            properties.setKVProperties(kvProperties);
                            dockerContainerTemplate.setProperties(properties);
                            topologyTemplate.addNodeTemplate(dockerContainerTemplate);
                            ModelUtilities.createRelationshipTemplateAndAddToTopology(dockerContainerTemplate,
                                dockerEngineTemplate,
                                ToscaBaseTypes.hostedOnRelationshipType,
                                topologyTemplate);
                            ModelUtilities.createRelationshipTemplateAndAddToTopology(dockerContainerTemplate,
                                kubernetesCluster,
                                Constants.deployedByRelationshipType,
                                topologyTemplate);
                        });
                    } catch (ApiException aE) {
                        logger.error("Error retrieving Pods", aE);
                    }
                }
            });
        } catch (ApiException aE) {
            logger.error("Error retrieving node list", aE);
        }

        updateGeneratedServiceTemplate(serviceTemplate);
    }

    @Override
    public void storeTransformedTOSCA() {
        Optional.ofNullable(retrieveGeneratedServiceTemplate()).ifPresent(toscaTransformer::save);
    }

    @Override
    public void cleanup() {
    }

    private void populateNodeTemplateProperties(TNodeTemplate nodeTemplate, Map<String, String> additionalProperties) {
        if (nodeTemplate.getProperties() != null && nodeTemplate.getProperties().getKVProperties() != null) {
            nodeTemplate.getProperties()
                .getKVProperties()
                .entrySet()
                .stream()
                .filter(entry -> !additionalProperties.containsKey(entry.getKey()) || additionalProperties.get(entry.getKey())
                    .isEmpty())
                .forEach(entry -> additionalProperties.put(entry.getKey(),
                    entry.getValue() != null && !entry.getValue()
                        .isEmpty() ? entry.getValue() : "get_input: " + entry.getKey() + "_" + nodeTemplate.getId()
                        .replaceAll("(\\s)|(:)|(\\.)", "_")));
        }

        // workaround to set new properties
        nodeTemplate.setProperties(new TEntityTemplate.Properties());
        nodeTemplate.getProperties().setKVProperties(additionalProperties);
    }
}
