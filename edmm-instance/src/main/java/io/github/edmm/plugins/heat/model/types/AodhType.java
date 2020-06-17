package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum AodhType {
    CompositeAlarm(ComponentType.Software_Component),
    EventAlarm(ComponentType.Compute),
    GnocchiAggregationByMetricsAlarm(ComponentType.Software_Component),
    GnocchiAggregationByResourcesAlarm(ComponentType.Software_Component),
    GnocchiResourcesAlarm(ComponentType.Software_Component),
    LBMemberHealthAlarm(ComponentType.Software_Component);

    private ComponentType componentType;

    AodhType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
