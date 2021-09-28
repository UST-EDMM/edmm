package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum AmplifyType {
    App(ComponentType.Software_Component),
    Branch(ComponentType.Software_Component),
    Domain(ComponentType.Software_Component);

    ComponentType componentType;

    AmplifyType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
