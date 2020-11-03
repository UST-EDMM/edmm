package io.github.edmm.plugins.heat.model;

import io.github.edmm.model.edimm.InstanceState;

public class StackStatus {
    public enum StackStatusForDeploymentInstance {
        CHECK_COMPLETE(InstanceState.InstanceStateForDeploymentInstance.CREATED),
        CREATE_COMPLETE(InstanceState.InstanceStateForDeploymentInstance.CREATED),
        CREATE_FAILED(InstanceState.InstanceStateForDeploymentInstance.ERROR),
        CREATE_IN_PROGRESS(InstanceState.InstanceStateForDeploymentInstance.CREATING),
        DELETE_COMPLETE(InstanceState.InstanceStateForDeploymentInstance.DELETED),
        DELETE_FAILED(InstanceState.InstanceStateForDeploymentInstance.ERROR),
        DELETE_IN_PROGRESS(InstanceState.InstanceStateForDeploymentInstance.DELETING),
        FAILED(InstanceState.InstanceStateForDeploymentInstance.ERROR),
        UPDATE_COMPLETE(InstanceState.InstanceStateForDeploymentInstance.UPDATED),
        UPDATE_IN_PROGRESS(InstanceState.InstanceStateForDeploymentInstance.UPDATING),
        TIMEOUT(InstanceState.InstanceStateForDeploymentInstance.ERROR),
        UNDEFINED(InstanceState.InstanceStateForDeploymentInstance.ERROR),
        UNKNOWN(InstanceState.InstanceStateForDeploymentInstance.ERROR),
        CHECK_FAILED(InstanceState.InstanceStateForDeploymentInstance.ERROR);

        private final InstanceState.InstanceStateForDeploymentInstance deploymentInstanceState;

        StackStatusForDeploymentInstance(InstanceState.InstanceStateForDeploymentInstance deploymentInstanceState) {
            this.deploymentInstanceState = deploymentInstanceState;
        }

        public InstanceState.InstanceStateForDeploymentInstance toEDIMMDeploymentInstanceState() {
            return deploymentInstanceState;
        }
    }

    public enum StackStatusForComponentInstance {
        CHECK_COMPLETE(InstanceState.InstanceStateForComponentInstance.CREATED),
        CREATE_COMPLETE(InstanceState.InstanceStateForComponentInstance.CREATED),
        CREATE_FAILED(InstanceState.InstanceStateForComponentInstance.ERROR),
        CREATE_IN_PROGRESS(InstanceState.InstanceStateForComponentInstance.CREATING),
        DELETE_COMPLETE(InstanceState.InstanceStateForComponentInstance.DELETED),
        DELETE_FAILED(InstanceState.InstanceStateForComponentInstance.ERROR),
        DELETE_IN_PROGRESS(InstanceState.InstanceStateForComponentInstance.DELETING),
        FAILED(InstanceState.InstanceStateForComponentInstance.ERROR),
        UPDATE_COMPLETE(InstanceState.InstanceStateForComponentInstance.UPDATED),
        UPDATE_IN_PROGRESS(InstanceState.InstanceStateForComponentInstance.CREATING),
        TIMEOUT(InstanceState.InstanceStateForComponentInstance.ERROR),
        UNDEFINED(InstanceState.InstanceStateForComponentInstance.ERROR),
        UNKNOWN(InstanceState.InstanceStateForComponentInstance.ERROR),
        CHECK_FAILED(InstanceState.InstanceStateForComponentInstance.ERROR);

        private final InstanceState.InstanceStateForComponentInstance componentInstanceState;

        StackStatusForComponentInstance(InstanceState.InstanceStateForComponentInstance componentInstanceState) {
            this.componentInstanceState = componentInstanceState;
        }

        public InstanceState.InstanceStateForComponentInstance toEDIMMComponentInstanceState() {
            return componentInstanceState;
        }
    }
}
