package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum DMSType {
    Certificate(ComponentType.Software_Component),
    Endpoint(ComponentType.Software_Component),
    EventSubscription(ComponentType.Software_Component),
    ReplicationInstance(ComponentType.Software_Component),
    ReplicationSubnetGroup(ComponentType.Software_Component),
    ReplicationTask(ComponentType.Software_Component);

    ComponentType componentType;

    DMSType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
