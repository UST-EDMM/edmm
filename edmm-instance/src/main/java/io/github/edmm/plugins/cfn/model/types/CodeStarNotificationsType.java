package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum CodeStarNotificationsType {
    NotificationRule(ComponentType.Software_Component);

    ComponentType componentType;

    CodeStarNotificationsType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
