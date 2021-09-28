package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum MistralType {
    CronTrigger(ComponentType.Software_Component),
    ExternalResource(ComponentType.Compute),
    Workflow(ComponentType.Software_Component);

    private ComponentType componentType;

    MistralType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
