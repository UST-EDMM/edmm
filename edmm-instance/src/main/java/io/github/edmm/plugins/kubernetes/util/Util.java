package io.github.edmm.plugins.kubernetes.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.Metadata;
import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.edimm.InstanceState;
import io.github.edmm.plugins.kubernetes.model.Status;
import io.kubernetes.client.models.V1DeploymentStatus;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodStatus;

public class Util {

    public static List<ComponentInstance> getComponentInstances(List<V1Pod> podList) {
        List<ComponentInstance> componentInstances = new ArrayList<>();
        podList.forEach(pod -> {
            ComponentInstance componentInstance = new ComponentInstance();
            componentInstance.setName(pod.getMetadata().getName());
            componentInstance.setType(pod.getMetadata().getLabels().get(KubernetesConstants.APP));
            componentInstance.setId(pod.getMetadata().getUid());
            componentInstance.setCreatedAt(String.valueOf(pod.getMetadata().getCreationTimestamp()));
            componentInstance.setState(getComponentInstanceState(pod.getStatus()));
            componentInstance.setMetadata(getMetadata(pod.getMetadata()));
            componentInstance.setInstanceProperties(getComponentInstanceProperties(pod.getStatus()));

            componentInstances.add(componentInstance);
        });
        return componentInstances;
    }

    public static List<InstanceProperty> getDeploymentInstanceProperties(V1DeploymentStatus deploymentStatus) {
        List<InstanceProperty> properties = new ArrayList<>();

        if (deploymentStatus.getAvailableReplicas() != null) {
            properties.add(new InstanceProperty(KubernetesConstants.AVAILABLE_REPLICAS, deploymentStatus.getAvailableReplicas().getClass().getSimpleName(), deploymentStatus.getAvailableReplicas()));
        }
        if (deploymentStatus.getCollisionCount() != null) {
            properties.add(new InstanceProperty(KubernetesConstants.COLLISION_COUNT, deploymentStatus.getCollisionCount().getClass().getSimpleName(), deploymentStatus.getCollisionCount()));
        }
        if (deploymentStatus.getObservedGeneration() != null) {
            properties.add(new InstanceProperty(KubernetesConstants.OBSERVED_GENERATION, deploymentStatus.getObservedGeneration().getClass().getSimpleName(), deploymentStatus.getObservedGeneration()));
        }
        if (deploymentStatus.getReadyReplicas() != null) {
            properties.add(new InstanceProperty(KubernetesConstants.READY_REPLICAS, deploymentStatus.getReadyReplicas().getClass().getSimpleName(), deploymentStatus.getReadyReplicas()));
        }
        if (deploymentStatus.getReplicas() != null) {
            properties.add(new InstanceProperty(KubernetesConstants.REPLICAS, deploymentStatus.getReplicas().getClass().getSimpleName(), deploymentStatus.getReplicas()));
        }
        if (deploymentStatus.getUnavailableReplicas() != null) {
            properties.add(new InstanceProperty(KubernetesConstants.UNAVAILABLE_REPLICAS, deploymentStatus.getUnavailableReplicas().getClass().getSimpleName(), deploymentStatus.getUnavailableReplicas()));
        }
        if (deploymentStatus.getUpdatedReplicas() != null) {
            properties.add(new InstanceProperty(KubernetesConstants.UPDATED_REPLICAS, deploymentStatus.getUpdatedReplicas().getClass().getSimpleName(), deploymentStatus.getUpdatedReplicas()));
        }

        return properties;
    }

    private static List<InstanceProperty> getComponentInstanceProperties(V1PodStatus podStatus) {
        List<InstanceProperty> properties = new ArrayList<>();

        if (podStatus.getContainerStatuses() != null) {
            podStatus.getContainerStatuses().forEach(containerStatus -> {
                if (containerStatus.getName() != null) {
                    properties.add(new InstanceProperty(KubernetesConstants.CONTAINER_STATUS + KubernetesConstants.KEY_DELIMITER + containerStatus.getName() + KubernetesConstants.KEY_DELIMITER + KubernetesConstants.NAME, containerStatus.getName().getClass().getSimpleName(), containerStatus.getName()));
                }
                if (containerStatus.getContainerID() != null) {
                    properties.add(new InstanceProperty(KubernetesConstants.CONTAINER_STATUS + KubernetesConstants.KEY_DELIMITER + containerStatus.getName() + KubernetesConstants.KEY_DELIMITER + KubernetesConstants.CONTAINER_ID, containerStatus.getContainerID().getClass().getSimpleName(), containerStatus.getContainerID()));
                }
                if (containerStatus.getImage() != null) {
                    properties.add(new InstanceProperty(KubernetesConstants.CONTAINER_STATUS + KubernetesConstants.KEY_DELIMITER + containerStatus.getName() + KubernetesConstants.KEY_DELIMITER + KubernetesConstants.IMAGE, containerStatus.getImage().getClass().getSimpleName(), containerStatus.getImage()));
                }
                if (containerStatus.getImageID() != null) {
                    properties.add(new InstanceProperty(KubernetesConstants.CONTAINER_STATUS + KubernetesConstants.KEY_DELIMITER + containerStatus.getName() + KubernetesConstants.KEY_DELIMITER + KubernetesConstants.IMAGE_ID, containerStatus.getImageID().getClass().getSimpleName(), containerStatus.getImageID()));
                }
                if (containerStatus.getRestartCount() != null) {
                    properties.add(new InstanceProperty(KubernetesConstants.CONTAINER_STATUS + KubernetesConstants.KEY_DELIMITER + containerStatus.getName() + KubernetesConstants.KEY_DELIMITER + KubernetesConstants.RESTART_COUNT, containerStatus.getRestartCount().getClass().getSimpleName(), containerStatus.getRestartCount()));
                }
                if (containerStatus.getState() != null) {
                    if (containerStatus.getState().getRunning() != null) {
                        properties.add(new InstanceProperty(KubernetesConstants.CONTAINER_STATUS + KubernetesConstants.KEY_DELIMITER + containerStatus.getName() + KubernetesConstants.KEY_DELIMITER + KubernetesConstants.STATE, String.class.getSimpleName(), KubernetesConstants.RUNNING));
                    }
                    if (containerStatus.getState().getWaiting() != null) {
                        properties.add(new InstanceProperty(KubernetesConstants.CONTAINER_STATUS + KubernetesConstants.KEY_DELIMITER + containerStatus.getName() + KubernetesConstants.KEY_DELIMITER + KubernetesConstants.STATE, String.class.getSimpleName(), KubernetesConstants.WAITING));
                    }
                    if (containerStatus.getState().getTerminated() != null) {
                        properties.add(new InstanceProperty(KubernetesConstants.CONTAINER_STATUS + KubernetesConstants.KEY_DELIMITER + containerStatus.getName() + KubernetesConstants.KEY_DELIMITER + KubernetesConstants.STATE, String.class.getSimpleName(), KubernetesConstants.TERMINATED));
                    }
                }
            });
        }
        if (podStatus.getHostIP() != null) {
            properties.add(new InstanceProperty(KubernetesConstants.HOST_IP, podStatus.getHostIP().getClass().getSimpleName(), podStatus.getHostIP()));
        }
        if (podStatus.getMessage() != null) {
            properties.add(new InstanceProperty(KubernetesConstants.MESSAGE, podStatus.getMessage().getClass().getSimpleName(), podStatus.getMessage()));
        }
        if (podStatus.getReason() != null) {
            properties.add(new InstanceProperty(KubernetesConstants.REASON, podStatus.getReason().getClass().getSimpleName(), podStatus.getReason()));
        }
        if (podStatus.getNominatedNodeName() != null) {
            properties.add(new InstanceProperty(KubernetesConstants.NOMINATED_NODE_NAME, podStatus.getNominatedNodeName().getClass().getSimpleName(), podStatus.getNominatedNodeName()));
        }
        if (podStatus.getPhase() != null) {
            properties.add(new InstanceProperty(KubernetesConstants.PHASE, podStatus.getPhase().getClass().getSimpleName(), podStatus.getPhase()));
        }
        if (podStatus.getPodIP() != null) {
            properties.add(new InstanceProperty(KubernetesConstants.POD_IP, podStatus.getPodIP().getClass().getSimpleName(), podStatus.getPodIP()));
        }
        if (podStatus.getQosClass() != null) {
            properties.add(new InstanceProperty(KubernetesConstants.QOS_CLASS, podStatus.getQosClass().getClass().getSimpleName(), podStatus.getQosClass()));
        }

        return properties;
    }

    /**
     * Get metadata from Kubernetes deployment and convert to EDiMM metadata object.
     *
     * @param kubernetesMetadata kubernetes deployment metadata
     * @return EDiMM metadata object containing all metadata
     */
    public static Metadata getMetadata(V1ObjectMeta kubernetesMetadata) {
        Map<String, Object> metadataMap = new LinkedHashMap<>();
        if (kubernetesMetadata.getAnnotations() != null) {
            kubernetesMetadata.getAnnotations().forEach((key, value) -> {
                if (!key.equals(KubernetesConstants.LAST_APPLIED_CONFIG)) {
                    metadataMap.put(key, value);
                }
            });
        }
        if (kubernetesMetadata.getClusterName() != null) {
            metadataMap.put(KubernetesConstants.CLUSTER_NAME, kubernetesMetadata.getClusterName());
        }
        if (kubernetesMetadata.getDeletionGracePeriodSeconds() != null) {
            metadataMap.put(KubernetesConstants.DELETION_GRACE_PERIOD_SECONDS, kubernetesMetadata.getDeletionGracePeriodSeconds());
        }
        if (kubernetesMetadata.getDeletionTimestamp() != null) {
            metadataMap.put(KubernetesConstants.DELETION_TIMESTAMP, kubernetesMetadata.getDeletionTimestamp());
        }
        if (kubernetesMetadata.getFinalizers() != null) {
            metadataMap.put(KubernetesConstants.FINALIZERS, kubernetesMetadata.getFinalizers());
        }
        if (kubernetesMetadata.getGenerateName() != null) {
            metadataMap.put(KubernetesConstants.GENERATE_NAME, kubernetesMetadata.getGenerateName());
        }
        if (kubernetesMetadata.getGeneration() != null) {
            metadataMap.put(KubernetesConstants.GENERATION, kubernetesMetadata.getGeneration());
        }
        if (kubernetesMetadata.getInitializers() != null) {
            metadataMap.put(KubernetesConstants.INITIALIZERS, kubernetesMetadata.getInitializers());
        }
        if (kubernetesMetadata.getLabels() != null) {
            kubernetesMetadata.getLabels().forEach(metadataMap::put);
        }
        if (kubernetesMetadata.getNamespace() != null) {
            metadataMap.put(KubernetesConstants.NAMESPACE, kubernetesMetadata.getNamespace());
        }
        if (kubernetesMetadata.getResourceVersion() != null) {
            metadataMap.put(KubernetesConstants.RESOURCE_VERSION, kubernetesMetadata.getResourceVersion());
        }

        return Metadata.of(metadataMap);
    }

    /**
     * Derive EDiMM instance state from Kubernetes deployment status.
     *
     * @param status kubernetes deployment status object
     * @return converted EDiMM deployment instance state value
     */
    public static InstanceState.InstanceStateForDeploymentInstance getDeploymentInstanceState(V1DeploymentStatus status) {

        if (Boolean.valueOf(status.getConditions().get(KubernetesConstants.LATEST_STATUS).getStatus())) {
            return Status.KubernetesDeploymentStatus.valueOf(String.valueOf(status.getConditions().get(0).getType())).toEDiMMDeploymentInstanceState();
        } else {
            return InstanceState.InstanceStateForDeploymentInstance.ERROR;
        }
    }

    /**
     * Derive EDiMM component instance state from Kubernetes Pod status.
     *
     * @param status kubernetes pod status object
     * @return converted EDiMM component instance state value
     */
    private static InstanceState.InstanceStateForComponentInstance getComponentInstanceState(V1PodStatus status) {

        if (Boolean.valueOf(status.getConditions().get(KubernetesConstants.LATEST_STATUS).getStatus())) {
            return Status.KubernetesPodStatus.valueOf(String.valueOf(status.getConditions().get(KubernetesConstants.LATEST_STATUS).getType())).toEDiMMComponentInstanceState();
        } else {
            return InstanceState.InstanceStateForComponentInstance.ERROR;
        }
    }
}
