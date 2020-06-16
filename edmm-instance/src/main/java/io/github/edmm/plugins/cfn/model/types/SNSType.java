package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum SNSType {
    Subscription(ComponentType.Software_Component),
    Topic(ComponentType.Software_Component),
    TopicPolicy(ComponentType.Software_Component);

    ComponentType componentType;

    SNSType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
