package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum TaaSType {
    TapFlow(ComponentType.Software_Component),
    TapService(ComponentType.Software_Component);

    private ComponentType componentType;

    TaaSType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
