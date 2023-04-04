package io.github.edmm.plugins.kubernetes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.core.transformation.TypeTransformer;
import io.github.edmm.exporter.WineryConnector;
import io.github.edmm.model.DeploymentTechnologyDescriptor;
import io.github.edmm.plugins.kubernetes.api.AuthenticatorImpl;
import io.github.edmm.plugins.kubernetes.typemapper.UbuntuMapper;
import io.github.edmm.util.Constants;
import io.github.edmm.util.Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1NodeList;
import io.kubernetes.client.models.V1NodeSystemInfo;
import io.kubernetes.client.models.V1PodList;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesInstancePlugin extends AbstractLifecycleInstancePlugin<KubernetesInstancePlugin> {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesInstancePlugin.class);
    private static final SourceTechnology KUBERNETES = SourceTechnology.builder()
        .id("kubernetes")
        .name("Kubernetes")
        .build();

    private final String kubeConfigPath;
    private final WineryConnector myWineryConnector;
    private final TOSCATransformer toscaTransformer;
    private final List<TypeTransformer> myHostTransformers;
    private final String targetNamespace;
    private final List<String> ignoredContainerNames;
    private CoreV1Api coreV1Api;
    private byte[] base64ConfigContents;

    public KubernetesInstancePlugin(
        InstanceTransformationContext context,
        String kubeConfigPath,
        String targetNamespace, List<String> ignoredContainerNames) {
        super(context);
        this.kubeConfigPath = kubeConfigPath;
        this.targetNamespace = targetNamespace;
        this.ignoredContainerNames = Objects.requireNonNull(ignoredContainerNames);
        this.myWineryConnector = WineryConnector.getInstance();
        myHostTransformers = Arrays.asList(new UbuntuMapper(myWineryConnector));
        toscaTransformer = new TOSCATransformer(Arrays.asList(new UbuntuMapper(myWineryConnector)));
    }

    @Override
    public void prepare() {
        AuthenticatorImpl authenticator = new AuthenticatorImpl(kubeConfigPath);
        authenticator.authenticate();

        this.coreV1Api = authenticator.getCoreV1Api();

        Path configFile = Paths.get(kubeConfigPath);
        byte[] configContents;
        try {
            configContents = Files.readAllBytes(configFile);
        } catch (IOException e) {
            throw new TransformationException("Could not load contents of kubeConfig file |" + kubeConfigPath + "|", e);
        }
        base64ConfigContents = Base64.getEncoder().encode(configContents);
    }

    @Override
    public void transformToTOSCA() {
        TServiceTemplate serviceTemplate = Optional.ofNullable(retrieveGeneratedServiceTemplate()).orElseGet(() -> {
            String serviceTemplateId = "kubernetes-" + this.targetNamespace;
            logger.info("Creating new service template for transformation |{}|", serviceTemplateId);
            return new TServiceTemplate.Builder(serviceTemplateId, new TTopologyTemplate()).setName(serviceTemplateId)
                .setTargetNamespace("http://opentosca.org/retrieved/instances")
                .addTag(new TTag.Builder("deploymentTechnology", KUBERNETES.getName()).build())
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
        List<DeploymentTechnologyDescriptor> deploymentTechnologies = Util.extractDeploymentTechnologiesFromServiceTemplate(
            serviceTemplate,
            objectMapper);

        DeploymentTechnologyDescriptor kubernetesTechnology = new DeploymentTechnologyDescriptor();
        kubernetesTechnology.setId("kubernetes-" + UUID.randomUUID());
        kubernetesTechnology.setTechnologyId(getContext().getSourceTechnology().getId());
        kubernetesTechnology.setManagedIds(Collections.emptyList());
        kubernetesTechnology.setProperties(Collections.emptyMap());

        String basePath = this.coreV1Api.getApiClient().getBasePath();
        Map<String, String> clusterProperties = new HashMap<>();
        clusterProperties.put(Constants.KUBERNETES_CLUSTER_IP, basePath);
        clusterProperties.put(Constants.KUBERNETES_KUBE_CONFIG_CONTENTS, new String(base64ConfigContents, StandardCharsets.UTF_8));
        clusterProperties.put(Constants.KUBERNETES_CLUSTER_NAMESPACE, targetNamespace);

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
                        }

                        v1PodList.getItems().forEach(v1Pod -> v1Pod.getSpec()
                            .getContainers()
                            .stream()
                            .filter(aV1Container -> !ignoredContainerNames.contains(aV1Container.getName()))
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
                                LinkedHashMap<String, String> kvProperties = Optional.ofNullable(dockerContainerTemplate.getProperties())
                                    .filter(prop -> prop instanceof TEntityTemplate.WineryKVProperties)
                                    .map(prop -> ((TEntityTemplate.WineryKVProperties) prop).getKVProperties())
                                    .orElseGet(LinkedHashMap::new);
                                kvProperties.put("ContainerID", name);
                                kvProperties.put("ImageID", image);
                                kvProperties.put(Constants.STATE, Constants.RUNNING);
                                kvProperties.put("PodName", v1Pod.getMetadata().getName());
                                if (StringUtils.isNotBlank(podIp)) {
                                    kvProperties.put("ContainerIP", podIp);
                                }
                                Util.populateNodeTemplateProperties(dockerContainerTemplate, kvProperties);
                                topologyTemplate.addNodeTemplate(dockerContainerTemplate);
                                ModelUtilities.createRelationshipTemplateAndAddToTopology(dockerContainerTemplate,
                                    dockerEngineTemplate,
                                    ToscaBaseTypes.hostedOnRelationshipType,
                                    topologyTemplate);
                                managedIds.add(dockerContainerTemplate.getId());
                            }));
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

        Util.updateDeploymentTechnologiesInServiceTemplate(serviceTemplate, objectMapper, deploymentTechnologies);

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
