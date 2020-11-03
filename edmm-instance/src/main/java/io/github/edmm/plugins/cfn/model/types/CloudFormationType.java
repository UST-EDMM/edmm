package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum CloudFormationType {
    CustomResource(ComponentType.Software_Component),
    Macro(ComponentType.Software_Component),
    Stack(ComponentType.Compute),
    WaitCondition(ComponentType.Software_Component),
    WaitConditionHandle(ComponentType.Software_Component);

    ComponentType componentType;

    CloudFormationType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
