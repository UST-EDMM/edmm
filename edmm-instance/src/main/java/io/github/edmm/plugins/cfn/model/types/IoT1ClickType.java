package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum IoT1ClickType {
    Device(ComponentType.Compute),
    Placement(ComponentType.Compute),
    Project(ComponentType.Software_Component);

    ComponentType componentType;

    IoT1ClickType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
