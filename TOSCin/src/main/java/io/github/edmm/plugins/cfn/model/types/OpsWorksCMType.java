package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum OpsWorksCMType {
    Server(ComponentType.Web_Server);

    ComponentType componentType;

    OpsWorksCMType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
