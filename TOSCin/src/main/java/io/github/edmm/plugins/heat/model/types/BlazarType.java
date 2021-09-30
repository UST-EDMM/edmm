package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum BlazarType {
    Host(ComponentType.Compute),
    Lease(ComponentType.Software_Component);

    private ComponentType componentType;

    BlazarType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
