package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum AthenaType {
    NamedQuery(ComponentType.Software_Component),
    WorkGroup(ComponentType.Software_Component);

    ComponentType componentType;

    AthenaType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
