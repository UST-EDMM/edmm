package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum LakeFormationType {
    DataLakeSettings(ComponentType.Software_Component),
    Permissions(ComponentType.Software_Component),
    Resource(ComponentType.DBaaS);

    ComponentType componentType;

    LakeFormationType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
