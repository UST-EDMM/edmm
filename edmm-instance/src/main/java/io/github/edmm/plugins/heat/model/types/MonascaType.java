package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum MonascaType {
    AlarmDefinition(ComponentType.Software_Component),
    Notification(ComponentType.Compute);

    private ComponentType componentType;

    MonascaType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
