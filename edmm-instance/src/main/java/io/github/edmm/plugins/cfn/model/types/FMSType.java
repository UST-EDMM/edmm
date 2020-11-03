package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum FMSType {
    NotificationChannel(ComponentType.Software_Component),
    Policy(ComponentType.Software_Component);

    ComponentType componentType;

    FMSType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
