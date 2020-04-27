package io.github.edmm.plugins.kubernetes.util;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.InstanceProperty;
import io.kubernetes.client.models.V1DeploymentStatus;
import io.kubernetes.client.models.V1PodStatus;

public class KubernetesPropertiesHandler {

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

    static List<InstanceProperty> getComponentInstanceProperties(V1PodStatus podStatus) {
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
}
