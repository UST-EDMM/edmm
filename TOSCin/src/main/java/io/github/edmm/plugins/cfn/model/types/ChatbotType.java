package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ChatbotType {
    SlackChannelConfiguration(ComponentType.Software_Component);

    ComponentType componentType;

    ChatbotType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
