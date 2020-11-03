package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum NovaType {
    Flavor(ComponentType.Software_Component),
    HostAggregate(ComponentType.Compute),
    KeyPair(ComponentType.Software_Component),
    Quota(ComponentType.Software_Component),
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
