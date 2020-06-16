package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum EFSType {
    AccessPoint(ComponentType.Software_Component),
    FileSystem(ComponentType.Software_Component),
    MountTarget(ComponentType.Software_Component);

    ComponentType componentType;

    EFSType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
