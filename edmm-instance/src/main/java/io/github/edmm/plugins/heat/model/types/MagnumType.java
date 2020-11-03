package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum MagnumType {
    Cluster(ComponentType.Compute),
    ClusterTemplate(ComponentType.Software_Component);

    private ComponentType componentType;

    MagnumType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
