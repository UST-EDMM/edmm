package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum DLMType {
    LifecyclePolicy(ComponentType.Software_Component);

    ComponentType componentType;

    DLMType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
