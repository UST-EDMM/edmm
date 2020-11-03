package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum GuardDutyType {
    Detector(ComponentType.Software_Component),
    Filter(ComponentType.Software_Component),
    IPSet(ComponentType.Software_Component),
    Master(ComponentType.Software_Component),
    Member(ComponentType.Software_Component),
    ThreatIntelSet(ComponentType.Software_Component);

    ComponentType componentType;

    GuardDutyType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
