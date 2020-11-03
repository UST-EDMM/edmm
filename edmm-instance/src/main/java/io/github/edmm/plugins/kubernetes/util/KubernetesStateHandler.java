package io.github.edmm.plugins.kubernetes.util;

import io.github.edmm.model.edimm.InstanceState;
import io.github.edmm.plugins.kubernetes.model.Status;

import io.kubernetes.client.models.V1DeploymentCondition;
import io.kubernetes.client.models.V1DeploymentStatus;
import io.kubernetes.client.models.V1PodCondition;
import io.kubernetes.client.models.V1PodStatus;

public abstract class KubernetesStateHandler {

    public static InstanceState.InstanceStateForDeploymentInstance getDeploymentInstanceState(V1DeploymentStatus status) {

        if (isKubernetesDeploymentStatusSetToTrue(status)) {
            return Status.KubernetesDeploymentStatus.valueOf(
                String.valueOf(getLatestDeploymentCondition(status).getType()))
                .toEDiMMDeploymentInstanceState();
        }
        return InstanceState.InstanceStateForDeploymentInstance.ERROR;
    }

    private static boolean isKubernetesDeploymentStatusSetToTrue(V1DeploymentStatus status) {
        return Boolean.valueOf(getLatestDeploymentStatus(status));
    }

    private static String getLatestDeploymentStatus(V1DeploymentStatus status) {
        return getLatestDeploymentCondition(status).getStatus();
    }

    private static V1DeploymentCondition getLatestDeploymentCondition(V1DeploymentStatus status) {
        return status.getConditions().get(KubernetesConstants.LATEST_STATUS);
    }

    static InstanceState.InstanceStateForComponentInstance getComponentInstanceState(V1PodStatus status) {

        if (isKubernetesPodStatusSetToTrue(status)) {
            return Status.KubernetesPodStatus.valueOf(
                String.valueOf(getLatestPodCondition(status).getType()))
                .toEDiMMComponentInstanceState();
        }
        return InstanceState.InstanceStateForComponentInstance.ERROR;
    }

    private static boolean isKubernetesPodStatusSetToTrue(V1PodStatus status) {
        return Boolean.valueOf(getLatestPodStatus(status));
    }

    private static String getLatestPodStatus(V1PodStatus status) {
        return getLatestPodCondition(status).getStatus();
    }

    private static V1PodCondition getLatestPodCondition(V1PodStatus status) {
        return status.getConditions().get(KubernetesConstants.LATEST_STATUS);
    }
}
