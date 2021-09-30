package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum SenlinType {
    Cluster(ComponentType.Compute),
    Node(ComponentType.Compute),
    Policy(ComponentType.Software_Component),
    Profile(ComponentType.Software_Component),
    Receiver(ComponentType.Software_Component);

    private ComponentType componentType;

    SenlinType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
