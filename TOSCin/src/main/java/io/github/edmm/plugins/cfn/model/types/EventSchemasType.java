package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum EventSchemasType {
    Discoverer(ComponentType.Software_Component),
    Registry(ComponentType.Software_Component),
    RegistryPolicy(ComponentType.Software_Component),
    Schema(ComponentType.Software_Component);

    ComponentType componentType;

    EventSchemasType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
