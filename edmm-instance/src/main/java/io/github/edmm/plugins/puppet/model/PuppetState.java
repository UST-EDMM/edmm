package io.github.edmm.plugins.puppet.model;

import io.github.edmm.model.edimm.InstanceState;

public class PuppetState {
    public enum NodeState {
        unchanged(InstanceState.InstanceStateForComponentInstance.CREATED),
        changed(InstanceState.InstanceStateForComponentInstance.UPDATED),
        failed(InstanceState.InstanceStateForComponentInstance.ERROR);

        private final InstanceState.InstanceStateForComponentInstance componentInstanceState;

        NodeState(InstanceState.InstanceStateForComponentInstance componentInstance) {
            this.componentInstanceState = componentInstance;
        }

        public InstanceState.InstanceStateForComponentInstance toEDIMMComponentInstanceState() {
            return componentInstanceState;
        }
    }

    public enum MasterStateAsComponentInstance {
        running(InstanceState.InstanceStateForComponentInstance.CREATED);

        private final InstanceState.InstanceStateForComponentInstance componentInstance;

        MasterStateAsComponentInstance(InstanceState.InstanceStateForComponentInstance componentInstance) {
            this.componentInstance = componentInstance;
        }

        public InstanceState.InstanceStateForComponentInstance toEDIMMComponentInstanceState() {
            return componentInstance;
        }
    }

    public enum MasterStateAsDeploymentInstance {
    }
}
