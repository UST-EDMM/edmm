package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum IoTThingsGraphType {
    FlowTemplate(ComponentType.Software_Component);

    ComponentType componentType;

    IoTThingsGraphType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
