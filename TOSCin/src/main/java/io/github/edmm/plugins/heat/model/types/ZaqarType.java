package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ZaqarType {
    MistralTrigger(ComponentType.Software_Component),
    Queue(ComponentType.Software_Component),
    SignedQueueURL(ComponentType.Software_Component),
    Subscription(ComponentType.Software_Component);

    private ComponentType componentType;

    ZaqarType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
