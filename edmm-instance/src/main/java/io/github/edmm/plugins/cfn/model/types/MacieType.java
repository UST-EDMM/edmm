package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum MacieType {
    CustomDataIdentifier(ComponentType.Software_Component),
    FindingsFilter(ComponentType.Software_Component),
    Session(ComponentType.Software_Component);

    ComponentType componentType;

    MacieType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
