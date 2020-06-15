package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

// TODO add all other types, these are only for validation of approach
public enum NovaType {
    KeyPair(ComponentType.Software_Component),
    Server(ComponentType.Compute),
    ServerGroup(ComponentType.Compute);

    private ComponentType componentType;

    NovaType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
