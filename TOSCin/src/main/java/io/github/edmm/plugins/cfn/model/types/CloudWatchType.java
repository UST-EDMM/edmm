package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum CloudWatchType {
    Alarm(ComponentType.Software_Component),
    AnomalyDetector(ComponentType.Software_Component),
    CompositeAlarm(ComponentType.Software_Component),
    Dashboard(ComponentType.Web_Application),
    InsightRule(ComponentType.Software_Component);

    ComponentType componentType;

    CloudWatchType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
