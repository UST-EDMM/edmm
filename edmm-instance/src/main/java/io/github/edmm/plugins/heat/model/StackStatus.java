package io.github.edmm.plugins.heat.model;

import io.github.edmm.model.edimm.InstanceState;

public class StackStatus {
    public enum StackStatusForDeploymentInstance {
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

        /**
         * Convert OpenStack HEAT state enum value to EDiMM instance state enum value.
         *
         * @return converted EDiMM state enum value
         */
        public InstanceState.InstanceStateForDeploymentInstance toEDIMMDeploymentInstanceState() {
            return deploymentInstanceState;
        }
    }

    public enum StackStatusForComponentInstance {
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

        /**
         * Convert OpenStack HEAT state enum value to EDiMM instance state enum value.
         *
         * @return converted EDiMM state enum value
         */
        public InstanceState.InstanceStateForComponentInstance toEDIMMComponentInstanceState() {
            return componentInstanceState;
        }
    }
}