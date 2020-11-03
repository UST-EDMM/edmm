package io.github.edmm.plugins.cfn.model;

import io.github.edmm.model.edimm.InstanceState;

public class Status {
    public enum CfnStackStatus {
        CREATE_COMPLETE(InstanceState.InstanceStateForDeploymentInstance.CREATED),
        CREATE_IN_PROGRESS(InstanceState.InstanceStateForDeploymentInstance.CREATING),
        CREATE_FAILED(InstanceState.InstanceStateForDeploymentInstance.ERROR),
        DELETE_COMPLETE(InstanceState.InstanceStateForDeploymentInstance.DELETED),
        DELETE_FAILED(InstanceState.InstanceStateForDeploymentInstance.ERROR),
        DELETE_IN_PROGRESS(InstanceState.InstanceStateForDeploymentInstance.DELETING),
        REVIEW_IN_PROGRESS(InstanceState.InstanceStateForDeploymentInstance.CREATING),
        ROLLBACK_COMPLETE(InstanceState.InstanceStateForDeploymentInstance.UPDATED),
        ROLLBACK_FAILED(InstanceState.InstanceStateForDeploymentInstance.ERROR),
        ROLLBACK_IN_PROGRESS(InstanceState.InstanceStateForDeploymentInstance.UPDATING),
        UPDATE_COMPLETE(InstanceState.InstanceStateForDeploymentInstance.UPDATED),
        UPDATE_COMPLETE_CLEANUP_IN_PROGRESS(InstanceState.InstanceStateForDeploymentInstance.UPDATING),
        UPDATE_IN_PROGRESS(InstanceState.InstanceStateForDeploymentInstance.UPDATING),
        UPDATE_ROLLBACK_COMPLETE(InstanceState.InstanceStateForDeploymentInstance.UPDATED),
        UPDATE_ROLLBACK_COMPLETE_CLEANUP_IN_PROGRESS(InstanceState.InstanceStateForDeploymentInstance.UPDATING),
        UPDATE_ROLLBACK_FAILED(InstanceState.InstanceStateForDeploymentInstance.ERROR),
        UPDATE_ROLLBACK_IN_PROGRESS(InstanceState.InstanceStateForDeploymentInstance.UPDATING),
        IMPORT_IN_PROGRESS(InstanceState.InstanceStateForDeploymentInstance.UPDATING),
        IMPORT_COMPLETE(InstanceState.InstanceStateForDeploymentInstance.CREATED),
        IMPORT_ROLLBACK_IN_PROGRESS(InstanceState.InstanceStateForDeploymentInstance.UPDATING),
        IMPORT_ROLLBACK_FAILED(InstanceState.InstanceStateForDeploymentInstance.ERROR),
        IMPORT_ROLLBACK_COMPLETE(InstanceState.InstanceStateForDeploymentInstance.UPDATED);

        private final InstanceState.InstanceStateForDeploymentInstance deploymentInstanceState;

        CfnStackStatus(InstanceState.InstanceStateForDeploymentInstance deploymentInstanceState) {
            this.deploymentInstanceState = deploymentInstanceState;
        }

        public InstanceState.InstanceStateForDeploymentInstance toEDiMMDeploymentInstanceState() {
            return this.deploymentInstanceState;
        }
    }

    public enum CfnStackResourceStatus {
        CREATE_COMPLETE(InstanceState.InstanceStateForComponentInstance.CREATED),
        CREATE_FAILED(InstanceState.InstanceStateForComponentInstance.ERROR),
        CREATE_IN_PROGRESS(InstanceState.InstanceStateForComponentInstance.CREATING),
        DELETE_COMPLETE(InstanceState.InstanceStateForComponentInstance.DELETED),
        DELETE_FAILED(InstanceState.InstanceStateForComponentInstance.ERROR),
        DELETE_IN_PROGRESS(InstanceState.InstanceStateForComponentInstance.DELETING),
        DELETE_SKIPPED(InstanceState.InstanceStateForComponentInstance.ERROR),
        IMPORT_COMPLETE(InstanceState.InstanceStateForComponentInstance.CREATED),
        IMPORT_FAILED(InstanceState.InstanceStateForComponentInstance.ERROR),
        IMPORT_IN_PROGRESS(InstanceState.InstanceStateForComponentInstance.UPDATING),
        IMPORT_ROLLBACK_COMPLETE(InstanceState.InstanceStateForComponentInstance.UPDATED),
        IMPORT_ROLLBACK_FAILED(InstanceState.InstanceStateForComponentInstance.ERROR),
        IMPORT_ROLLBACK_IN_PROGRESS(InstanceState.InstanceStateForComponentInstance.UPDATING),
        UPDATE_COMPLETE(InstanceState.InstanceStateForComponentInstance.UPDATED),
        UPDATE_FAILED(InstanceState.InstanceStateForComponentInstance.ERROR),
        UPDATE_IN_PROGRESS(InstanceState.InstanceStateForComponentInstance.UPDATING),;

        private final InstanceState.InstanceStateForComponentInstance componentInstanceState;

        CfnStackResourceStatus(InstanceState.InstanceStateForComponentInstance componentInstanceState) {
            this.componentInstanceState = componentInstanceState;
        }

        public InstanceState.InstanceStateForComponentInstance toEDiMMComponentInstanceState() {
            return this.componentInstanceState;
        }
    }
}
