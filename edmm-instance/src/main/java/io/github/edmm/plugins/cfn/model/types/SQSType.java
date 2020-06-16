package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum SQSType {
    Queue(ComponentType.Software_Component),
    QueuePolicy(ComponentType.Software_Component);

    ComponentType componentType;

    SQSType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
