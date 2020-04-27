package io.github.edmm.plugins.kubernetes.util;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.edimm.InstanceProperty;
import io.kubernetes.client.models.V1DeploymentStatus;

public class KubernetesDeploymentPropertiesHandler {
    private final List<InstanceProperty> instanceProperties = new ArrayList<>();
    private final V1DeploymentStatus deploymentStatus;

    public KubernetesDeploymentPropertiesHandler(V1DeploymentStatus deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
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
}
