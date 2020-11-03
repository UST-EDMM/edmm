package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ApplicationAutoScalingType {
    ScalableTarget(ComponentType.Compute),
    ScalingPolicy(ComponentType.Software_Component);

    ComponentType componentType;

    ApplicationAutoScalingType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
