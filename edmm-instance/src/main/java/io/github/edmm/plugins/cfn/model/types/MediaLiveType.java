package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum MediaLiveType {
    Channel(ComponentType.Software_Component),
    Input(ComponentType.Software_Component),
    InputSecurityGroup(ComponentType.Platform);

    ComponentType componentType;

    MediaLiveType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
