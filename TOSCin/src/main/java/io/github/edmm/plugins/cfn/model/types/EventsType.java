package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum EventsType {
    EventBus(ComponentType.Software_Component),
    EventBusPolicy(ComponentType.Software_Component),
    Rule(ComponentType.Software_Component);

    ComponentType componentType;

    EventsType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
