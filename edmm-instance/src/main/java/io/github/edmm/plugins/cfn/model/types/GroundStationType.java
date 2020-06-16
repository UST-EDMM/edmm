package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum GroundStationType {
    Config(ComponentType.Software_Component),
    DataflowEndpointGroup(ComponentType.Software_Component),
    MissionProfile(ComponentType.Software_Component);

    ComponentType componentType;

    GroundStationType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
