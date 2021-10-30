package io.github.edmm.plugins.kubernetes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.transformation.TypeTransformer;
import io.github.edmm.exporter.WineryConnector;
import io.github.edmm.model.ToscaDeploymentTechnology;
import io.github.edmm.plugins.kubernetes.api.ApiInteractorImpl;
import io.github.edmm.plugins.kubernetes.api.AuthenticatorImpl;
import io.github.edmm.plugins.kubernetes.typemapper.UbuntuMapper;
import io.github.edmm.util.Constants;
import io.github.edmm.util.Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Container;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1NodeList;
import io.kubernetes.client.models.V1NodeSystemInfo;
import io.kubernetes.client.models.V1PodList;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesInstancePlugin extends AbstractLifecycleInstancePlugin<KubernetesInstancePlugin> {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesInstancePlugin.class);
    private static final List<String> IGNORED_CONTAINER_NAMES = Arrays.asList("orders",
        "payment",
        "queue-master",
        "rabbitmq",
        "rabbitmq-exporter",
        "session-db",
        "shipping",
        "orders-db");
    private static final SourceTechnology KUBERNETES = SourceTechnology.builder()
        .id("kubernetes")
        .name("Kubernetes")
        .build();

    private final String kubeConfigPath;
    private final WineryConnector myWineryConnector;
    private final TOSCATransformer toscaTransformer;
    private final List<TypeTransformer> myHostTransformers;
    private final String inputDeploymentName;
    private final String targetNamespace;
    private AppsV1Api appsApi;
    private CoreV1Api coreV1Api;
    private V1Deployment kubernetesDeploymentInstance;

    public KubernetesInstancePlugin(
        InstanceTransformationContext context,
        String kubeConfigPath,
        String inputDeploymentName,
        String targetNamespace) {
        super(context);
        this.kubeConfigPath = kubeConfigPath;
        this.inputDeploymentName = inputDeploymentName;
        this.targetNamespace = targetNamespace;
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

        ApiInteractorImpl apiInteractor = new ApiInteractorImpl(this.appsApi, this.coreV1Api, this.inputDeploymentName);
        this.kubernetesDeploymentInstance = apiInteractor.getDeployment();
    }

    @Override
    public void transformToTOSCA() {
        TServiceTemplate serviceTemplate = Optional.ofNullable(retrieveGeneratedServiceTemplate()).orElseGet(() -> {
            TTopologyTemplate topologyTemplate = new TTopologyTemplate();
            String serviceTemplateId = "kubernetes-" + this.kubernetesDeploymentInstance.getMetadata().getName();
            logger.info("Creating new service template for transformation |{}|", serviceTemplateId);
            return new TServiceTemplate.Builder(serviceTemplateId, topologyTemplate).setName(serviceTemplateId)
                .setTargetNamespace("http://opentosca.org/retrieved/instances")
                .addTags(new TTags.Builder().addTag("deploymentTechnology", KUBERNETES.getName()).build())
                .build();
        });

        TTopologyTemplate topologyTemplate = Optional.ofNullable(serviceTemplate.getTopologyTemplate())
            .orElseGet(() -> {
                logger.info("Creating new topology template, as existing service template has none");
                TTopologyTemplate topologyTemplate1 = new TTopologyTemplate();
                serviceTemplate.setTopologyTemplate(topologyTemplate1);
                return topologyTemplate1;
            });

        ObjectMapper objectMapper = new ObjectMapper();
        List<ToscaDeploymentTechnology> deploymentTechnologies = Util.extractDeploymentTechnologiesFromServiceTemplate(
            serviceTemplate,
            objectMapper);

        ToscaDeploymentTechnology kubernetesTechnology = new ToscaDeploymentTechnology();
        kubernetesTechnology.setId("kubernetes-" + UUID.randomUUID());
        kubernetesTechnology.setSourceTechnology(getContext().getSourceTechnology());
        kubernetesTechnology.setManagedIds(Collections.emptyList());
        kubernetesTechnology.setProperties(Collections.emptyMap());

        String basePath = this.coreV1Api.getApiClient().getBasePath();
        Map<String, String> clusterProperties = new HashMap<>();
        clusterProperties.put(Constants.KUBERNETES_CLUSTER_IP, basePath);

        kubernetesTechnology.setProperties(clusterProperties);

        deploymentTechnologies.add(kubernetesTechnology);

        List<String> managedIds = new ArrayList<>();
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
                    managedIds.add(dockerEngineTemplate.getId());

                    try {
                        List<V1Container> containers;
                        V1PodList v1PodList;
                        if (this.targetNamespace != null) {
                            v1PodList = this.coreV1Api.listNamespacedPod(targetNamespace,
                                false,
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
                        } else {
                            v1PodList = this.coreV1Api.listPodForAllNamespaces(null,
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

                        v1PodList.getItems().stream().forEach(v1Pod -> {
                            v1Pod.getSpec()
                                .getContainers()
                                .stream()
                                .filter(aV1Container -> !IGNORED_CONTAINER_NAMES.contains(aV1Container.getName()))
                                .forEach(aV1Container -> {
                                    String image = aV1Container.getImage();
                                    String name = aV1Container.getName();
                                    String podIp = v1Pod.getStatus().getPodIP();
                                    TNodeType dockerContainerType = toscaTransformer.getComputeNodeType(
                                        "DockerContainer",
                                        "");
                                    TNodeTemplate dockerContainerTemplate = ModelUtilities.instantiateNodeTemplate(
                                        dockerContainerType);
                                    dockerContainerTemplate.setName(name);
                                    LinkedHashMap<String, String> kvProperties = Optional.ofNullable(
                                            dockerContainerTemplate.getProperties())
                                        .map(TEntityTemplate.Properties::getKVProperties)
                                        .orElseGet(LinkedHashMap::new);
                                    kvProperties.put("ContainerID", name);
                                    kvProperties.put("ImageID", image);
                                    if (StringUtils.isNotBlank(podIp)) {
                                        kvProperties.put("ContainerIP", podIp);
                                    }
                                    TEntityTemplate.Properties properties = Optional.ofNullable(dockerContainerTemplate.getProperties())
                                        .orElseGet(TEntityTemplate.Properties::new);
                                    properties.setKVProperties(kvProperties);
                                    dockerContainerTemplate.setProperties(properties);
                                    topologyTemplate.addNodeTemplate(dockerContainerTemplate);
                                    ModelUtilities.createRelationshipTemplateAndAddToTopology(dockerContainerTemplate,
                                        dockerEngineTemplate,
                                        ToscaBaseTypes.hostedOnRelationshipType,
                                        topologyTemplate);
                                    managedIds.add(dockerContainerTemplate.getId());
                                });
                        });
                    } catch (ApiException aE) {
                        logger.error("Error retrieving Pods", aE);
                    }
                }
            });
        } catch (ApiException aE) {
            logger.error("Error retrieving node list", aE);
        }

        managedIds.addAll(kubernetesTechnology.getManagedIds());
        kubernetesTechnology.setManagedIds(managedIds);

        Util.updateDeploymenTechnologiesInServiceTemplate(serviceTemplate, objectMapper, deploymentTechnologies);

        updateGeneratedServiceTemplate(serviceTemplate);
    }

    @Override
    public void storeTransformedTOSCA() {
        Optional.ofNullable(retrieveGeneratedServiceTemplate()).ifPresent(toscaTransformer::save);
    }

    @Override
    public void cleanup() {
    }
}
