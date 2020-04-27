package io.github.edmm.plugins.kubernetes.model;

import io.github.edmm.model.edimm.InstanceState;

public class Status {
    public enum KubernetesDeploymentStatus {
        Available(InstanceState.InstanceStateForDeploymentInstance.CREATED),
        Progressing(InstanceState.InstanceStateForDeploymentInstance.CREATING),
        ReplicaFailure(InstanceState.InstanceStateForDeploymentInstance.ERROR);

        private final InstanceState.InstanceStateForDeploymentInstance deploymentInstanceState;

        KubernetesDeploymentStatus(InstanceState.InstanceStateForDeploymentInstance deploymentInstanceState) {
            this.deploymentInstanceState = deploymentInstanceState;
        }

        public InstanceState.InstanceStateForDeploymentInstance toEDiMMDeploymentInstanceState() {
            return this.deploymentInstanceState;
        }
    }

    public enum KubernetesPodStatus {
        PodScheduled(InstanceState.InstanceStateForComponentInstance.CREATING),
        ContainersReady(InstanceState.InstanceStateForComponentInstance.CREATING),
        Ready(InstanceState.InstanceStateForComponentInstance.CREATING),
        Initialized(InstanceState.InstanceStateForComponentInstance.CREATED);

        private final InstanceState.InstanceStateForComponentInstance componentInstanceState;

        KubernetesPodStatus(InstanceState.InstanceStateForComponentInstance componentInstanceState) {
            this.componentInstanceState = componentInstanceState;
        }

        public InstanceState.InstanceStateForComponentInstance toEDiMMComponentInstanceState() {
            return this.componentInstanceState;
        }
    }
}
