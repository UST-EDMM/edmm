package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum IoTEventsType {
    DetectorModel(ComponentType.Software_Component),
    Input(ComponentType.Software_Component);

    ComponentType componentType;

    IoTEventsType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
