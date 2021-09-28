package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum DesignateType {
    RecordSet(ComponentType.Software_Component),
    Zone(ComponentType.Software_Component);

    private ComponentType componentType;

    DesignateType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
