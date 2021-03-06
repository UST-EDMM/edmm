package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum RAMType {
    ResourceShare(ComponentType.Software_Component);

    ComponentType componentType;

    RAMType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
