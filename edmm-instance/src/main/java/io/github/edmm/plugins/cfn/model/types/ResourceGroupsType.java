package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ResourceGroupsType {
    Group(ComponentType.Software_Component);

    ComponentType componentType;

    ResourceGroupsType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
