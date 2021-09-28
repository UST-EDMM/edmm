package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ManagedBlockchainType {
    Member(ComponentType.Software_Component),
    Node(ComponentType.Compute);

    ComponentType componentType;

    ManagedBlockchainType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
