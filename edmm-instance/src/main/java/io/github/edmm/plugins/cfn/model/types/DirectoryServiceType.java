package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum DirectoryServiceType {
    MicrosoftAD(ComponentType.Software_Component),
    SimpleAD(ComponentType.Software_Component);

    ComponentType componentType;

    DirectoryServiceType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
