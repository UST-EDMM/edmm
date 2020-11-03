package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum BatchType {
    ComputeEnvironment(ComponentType.Compute),
    JobDefinition(ComponentType.Software_Component),
    JobQueue(ComponentType.Software_Component);

    ComponentType componentType;

    BatchType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
