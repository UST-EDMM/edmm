package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum TroveType {
    Cluster(ComponentType.Database),
    Instance(ComponentType.Database);

    private ComponentType componentType;

    TroveType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
