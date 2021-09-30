package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum PinpointEmailType {
    ConfigurationSet(ComponentType.Software_Component),
    ConfigurationSetEventDestination(ComponentType.Software_Component),
    DedicatedIpPool(ComponentType.Software_Component),
    Identity(ComponentType.Software_Component);

    ComponentType componentType;

    PinpointEmailType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
