package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum Cloud9Type {
    EnvironmentEC2(ComponentType.PaaS);

    ComponentType componentType;

    Cloud9Type(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
