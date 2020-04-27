package io.github.edmm.plugins.kubernetes.util;

import io.github.edmm.model.edimm.InstanceState;
import io.github.edmm.plugins.kubernetes.model.Status;
import io.kubernetes.client.models.V1DeploymentStatus;
import io.kubernetes.client.models.V1PodStatus;

public class KubernetesStateHandler {

    public static InstanceState.InstanceStateForDeploymentInstance getDeploymentInstanceState(V1DeploymentStatus status) {

        if (Boolean.valueOf(status.getConditions().get(KubernetesConstants.LATEST_STATUS).getStatus())) {
            return Status.KubernetesDeploymentStatus.valueOf(String.valueOf(status.getConditions().get(0).getType())).toEDiMMDeploymentInstanceState();
        } else {
            return InstanceState.InstanceStateForDeploymentInstance.ERROR;
        }
    }

    protected static InstanceState.InstanceStateForComponentInstance getComponentInstanceState(V1PodStatus status) {

        if (Boolean.valueOf(status.getConditions().get(KubernetesConstants.LATEST_STATUS).getStatus())) {
            return Status.KubernetesPodStatus.valueOf(String.valueOf(status.getConditions().get(KubernetesConstants.LATEST_STATUS).getType())).toEDiMMComponentInstanceState();
        } else {
            return InstanceState.InstanceStateForComponentInstance.ERROR;
        }
    }
}
