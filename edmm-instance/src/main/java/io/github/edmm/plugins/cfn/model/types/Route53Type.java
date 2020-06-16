package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum Route53Type {
    HealthCheck(ComponentType.Software_Component),
    HostedZone(ComponentType.Software_Component),
    RecordSet(ComponentType.Software_Component),
    RecordSetGroup(ComponentType.Software_Component);

    ComponentType componentType;

    Route53Type(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
