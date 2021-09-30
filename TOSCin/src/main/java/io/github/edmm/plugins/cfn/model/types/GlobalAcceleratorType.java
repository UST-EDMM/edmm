package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum GlobalAcceleratorType {
    Accelerator(ComponentType.Software_Component),
    EndpointGroup(ComponentType.Web_Server),
    Listener(ComponentType.Compute);

    ComponentType componentType;

    GlobalAcceleratorType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
