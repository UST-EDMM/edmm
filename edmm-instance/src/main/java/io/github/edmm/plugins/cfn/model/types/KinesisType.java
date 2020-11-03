package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum KinesisType {
    Stream(ComponentType.Software_Component),
    StreamConsumer(ComponentType.Software_Component);

    ComponentType componentType;

    KinesisType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
