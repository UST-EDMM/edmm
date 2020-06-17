package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ManilaType {
    SecurityService(ComponentType.Software_Component),
    Share(ComponentType.Software_Component),
    ShareNetwork(ComponentType.Software_Component),
    ShareType(ComponentType.Software_Component);

    private ComponentType componentType;

    ManilaType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
