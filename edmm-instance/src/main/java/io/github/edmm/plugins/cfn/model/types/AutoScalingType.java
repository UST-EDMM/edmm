package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum AutoScalingType {
    AutoScalingGroup(ComponentType.Compute),
    LaunchConfiguration(ComponentType.Software_Component),
    LifecycleHook(ComponentType.Software_Component),
    ScalingPolicy(ComponentType.Software_Component),
    ScheduledAction(ComponentType.Software_Component);


    ComponentType componentType;

    AutoScalingType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
