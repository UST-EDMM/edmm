package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum AutoScalingPlansType {
    ScalingPlan(ComponentType.Software_Component);

    ComponentType componentType;

    AutoScalingPlansType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
