package io.github.edmm.model.edimm;

import io.github.edmm.model.opentosca.TOSCAState;

public class InstanceState {
    public enum InstanceStateForDeploymentInstance {
        CREATING(TOSCAState.ServiceTemplateInstanceState.CREATING),
        CREATED(TOSCAState.ServiceTemplateInstanceState.CREATED),
        DELETING(TOSCAState.ServiceTemplateInstanceState.DELETING),
        DELETED(TOSCAState.ServiceTemplateInstanceState.DELETED),
        ERROR(TOSCAState.ServiceTemplateInstanceState.ERROR),
        UPDATED(TOSCAState.ServiceTemplateInstanceState.MIGRATED),
        UPDATING(TOSCAState.ServiceTemplateInstanceState.MIGRATING);

        private final TOSCAState.ServiceTemplateInstanceState serviceTemplateInstanceState;

        InstanceStateForDeploymentInstance(TOSCAState.ServiceTemplateInstanceState toscaState) {
            this.serviceTemplateInstanceState = toscaState;
        }

        /**
         * Convert enum value of EDiMM instance state to OpenTOSCA instance state enum value.
         *
         * @return value of OpenTOSCA state enum
         */
        public TOSCAState.ServiceTemplateInstanceState toTOSCAServiceTemplateInstanceState() {
            return serviceTemplateInstanceState;
        }
    }

    public enum InstanceStateForComponentInstance {
        CREATING(TOSCAState.NodeTemplateInstanceState.CREATING),
        CREATED(TOSCAState.NodeTemplateInstanceState.CREATED),
        DELETING(TOSCAState.NodeTemplateInstanceState.DELETING),
        DELETED(TOSCAState.NodeTemplateInstanceState.DELETED),
        ERROR(TOSCAState.NodeTemplateInstanceState.ERROR),
        UPDATED(TOSCAState.NodeTemplateInstanceState.MIGRATED),
        STARTED(TOSCAState.NodeTemplateInstanceState.STARTED),
        STOPPED(TOSCAState.NodeTemplateInstanceState.STOPPED);

        private final TOSCAState.NodeTemplateInstanceState nodeTemplateInstanceState;

        InstanceStateForComponentInstance(TOSCAState.NodeTemplateInstanceState toscaState) {
            this.nodeTemplateInstanceState = toscaState;
        }

        /**
         * Convert enum value of EDiMM instance state to OpenTOSCA instance state enum value.
         *
         * @return value of OpenTOSCA state enum
         */
        public TOSCAState.NodeTemplateInstanceState toTOSCANodeTemplateInstanceState() {
            return nodeTemplateInstanceState;
        }
    }
}
