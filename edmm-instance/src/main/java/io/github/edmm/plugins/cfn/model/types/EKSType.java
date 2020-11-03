package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum EKSType {
    Cluster(ComponentType.Compute),
    Nodegroup(ComponentType.Compute);

    ComponentType componentType;

    EKSType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
