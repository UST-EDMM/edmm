package io.github.edmm.plugins.kubernetes.util;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.InstanceProperty;
import io.kubernetes.client.models.V1ContainerStatus;
import io.kubernetes.client.models.V1DeploymentStatus;
import io.kubernetes.client.models.V1PodStatus;

public class KubernetesPropertiesHandler {
    List<InstanceProperty> instanceProperties = new ArrayList<>();
    V1DeploymentStatus deploymentStatus;
    V1PodStatus podStatus;

    public KubernetesPropertiesHandler(V1DeploymentStatus deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    public KubernetesPropertiesHandler(V1PodStatus podStatus) {
        this.podStatus = podStatus;
    }

    public List<InstanceProperty> getDeploymentInstanceProperties() {
        handleAvailableReplicas();
        handleCollisionCount();
        handleObservedGeneration();
        handleReadyReplicas();
        handleReplicas();
        handleUnavailableReplicas();
        handleUpdatedReplicas();

        return this.instanceProperties;
    }

    private void handleAvailableReplicas() {
        if (this.deploymentStatus.getAvailableReplicas() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            KubernetesConstants.AVAILABLE_REPLICAS,
            this.deploymentStatus.getAvailableReplicas().getClass().getSimpleName(),
            this.deploymentStatus.getAvailableReplicas())
        );
    }

    private void handleCollisionCount() {
        if (this.deploymentStatus.getCollisionCount() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            KubernetesConstants.COLLISION_COUNT,
            this.deploymentStatus.getCollisionCount().getClass().getSimpleName(),
            this.deploymentStatus.getCollisionCount())
        );
    }

    private void handleObservedGeneration() {
        if (this.deploymentStatus.getObservedGeneration() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            KubernetesConstants.OBSERVED_GENERATION,
            this.deploymentStatus.getObservedGeneration().getClass().getSimpleName(),
            this.deploymentStatus.getObservedGeneration())
        );
    }

    private void handleReadyReplicas() {
        if (this.deploymentStatus.getReadyReplicas() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            KubernetesConstants.READY_REPLICAS,
            this.deploymentStatus.getReadyReplicas().getClass().getSimpleName(),
            this.deploymentStatus.getReadyReplicas())
        );
    }

    private void handleReplicas() {
        if (this.deploymentStatus.getReplicas() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            KubernetesConstants.REPLICAS,
            this.deploymentStatus.getReplicas().getClass().getSimpleName(),
            this.deploymentStatus.getReplicas())
        );
    }

    private void handleUnavailableReplicas() {
        if (this.deploymentStatus.getUnavailableReplicas() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            KubernetesConstants.UNAVAILABLE_REPLICAS,
            this.deploymentStatus.getUnavailableReplicas().getClass().getSimpleName(),
            this.deploymentStatus.getUnavailableReplicas())
        );
    }

    private void handleUpdatedReplicas() {
        if (this.deploymentStatus.getUpdatedReplicas() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            KubernetesConstants.UPDATED_REPLICAS,
            this.deploymentStatus.getUpdatedReplicas().getClass().getSimpleName(),
            this.deploymentStatus.getUpdatedReplicas())
        );
    }

    List<InstanceProperty> getComponentInstanceProperties() {
        handleContainerStatuses();
        handleHostIP();
        handleMessage();
        handleReason();
        handleNominatedNodeName();
        handlePhase();
        handlePodIP();
        handleQoSClass();

        return this.instanceProperties;
    }

    private void handleHostIP() {
        if (this.podStatus.getHostIP() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            KubernetesConstants.HOST_IP,
            this.podStatus.getHostIP().getClass().getSimpleName(),
            this.podStatus.getHostIP())
        );
    }

    private void handleMessage() {
        if (this.podStatus.getMessage() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            KubernetesConstants.MESSAGE,
            this.podStatus.getMessage().getClass().getSimpleName(),
            this.podStatus.getMessage())
        );
    }

    private void handleReason() {
        if (this.podStatus.getReason() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            KubernetesConstants.REASON,
            this.podStatus.getReason().getClass().getSimpleName(),
            this.podStatus.getReason())
        );
    }

    private void handleNominatedNodeName() {
        if (this.podStatus.getNominatedNodeName() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            KubernetesConstants.NOMINATED_NODE_NAME,
            this.podStatus.getNominatedNodeName().getClass().getSimpleName(),
            this.podStatus.getNominatedNodeName())
        );
    }

    private void handlePhase() {
        if (this.podStatus.getPhase() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            KubernetesConstants.PHASE,
            this.podStatus.getPhase().getClass().getSimpleName(),
            this.podStatus.getPhase())
        );
    }

    private void handlePodIP() {
        if (this.podStatus.getPodIP() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            KubernetesConstants.POD_IP,
            this.podStatus.getPodIP().getClass().getSimpleName(),
            this.podStatus.getPodIP())
        );
    }

    private void handleQoSClass() {
        if (this.podStatus.getQosClass() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            KubernetesConstants.QOS_CLASS,
            this.podStatus.getQosClass().getClass().getSimpleName(),
            this.podStatus.getQosClass()));
    }

    private void handleContainerStatuses() {
        if (this.podStatus.getContainerStatuses() == null) {
            return;
        }
        this.podStatus.getContainerStatuses().forEach(this::handleContainerStatus);
    }

    private void handleContainerStatus(V1ContainerStatus containerStatus) {
        handleContainerStatusName(containerStatus);
        handleContainerStatusID(containerStatus);
        handleContainerStatusImage(containerStatus);
        handleContainerStatusImageID(containerStatus);
        handleContainerStatusRestartCount(containerStatus);
        handleContainerStatusState(containerStatus);
    }

    private void handleContainerStatusName(V1ContainerStatus containerStatus) {
        if (containerStatus.getName() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            generatePropertyKey(containerStatus.getName(), KubernetesConstants.NAME),
            containerStatus.getName().getClass().getSimpleName(),
            containerStatus.getName())
        );
    }

    private void handleContainerStatusID(V1ContainerStatus containerStatus) {
        if (containerStatus.getContainerID() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            generatePropertyKey(containerStatus.getName(), KubernetesConstants.CONTAINER_ID),
            containerStatus.getContainerID().getClass().getSimpleName(),
            containerStatus.getContainerID())
        );
    }

    private void handleContainerStatusImage(V1ContainerStatus containerStatus) {
        if (containerStatus.getImage() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            generatePropertyKey(containerStatus.getName(), KubernetesConstants.IMAGE),
            containerStatus.getImage().getClass().getSimpleName(),
            containerStatus.getImage())
        );
    }

    private void handleContainerStatusImageID(V1ContainerStatus containerStatus) {
        if (containerStatus.getImageID() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            generatePropertyKey(containerStatus.getName(), KubernetesConstants.IMAGE_ID),
            containerStatus.getImageID().getClass().getSimpleName(),
            containerStatus.getImageID())
        );
    }

    private void handleContainerStatusRestartCount(V1ContainerStatus containerStatus) {
        if (containerStatus.getRestartCount() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            generatePropertyKey(containerStatus.getName(), KubernetesConstants.RESTART_COUNT),
            containerStatus.getRestartCount().getClass().getSimpleName(),
            containerStatus.getRestartCount()));
    }

    private void handleContainerStatusState(V1ContainerStatus containerStatus) {
        if (containerStatus.getState() == null) {
            return;
        }
        handleRunningState(containerStatus);
        handleWaitingState(containerStatus);
        handleTerminatedState(containerStatus);
    }

    private void handleRunningState(V1ContainerStatus containerStatus) {
        if (containerStatus.getState().getRunning() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            generatePropertyKey(containerStatus.getName(), KubernetesConstants.STATE),
            String.class.getSimpleName(),
            KubernetesConstants.RUNNING)
        );
    }

    private void handleWaitingState(V1ContainerStatus containerStatus) {
        if (containerStatus.getState().getWaiting() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            generatePropertyKey(containerStatus.getName(), KubernetesConstants.STATE),
            String.class.getSimpleName(),
            KubernetesConstants.WAITING)
        );
    }

    private void handleTerminatedState(V1ContainerStatus containerStatus) {
        if (containerStatus.getState().getTerminated() == null) {
            return;
        }
        this.instanceProperties.add(new InstanceProperty(
            generatePropertyKey(containerStatus.getName(), KubernetesConstants.STATE),
            String.class.getSimpleName(),
            KubernetesConstants.TERMINATED));
    }

    private String generatePropertyKey(String containerStatusName, String key) {
        return KubernetesConstants.CONTAINER_STATUS + KubernetesConstants.KEY_DELIMITER + containerStatusName
            + KubernetesConstants.KEY_DELIMITER + key;
    }
}
