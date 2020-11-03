package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum KMSType {
    Alias(ComponentType.Software_Component),
    Key(ComponentType.Software_Component);

    ComponentType componentType;

    KMSType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
