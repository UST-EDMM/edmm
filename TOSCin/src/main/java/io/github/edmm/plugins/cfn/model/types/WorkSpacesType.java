package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum WorkSpacesType {
    Workspace(ComponentType.Software_Component);

    ComponentType componentType;

    WorkSpacesType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
