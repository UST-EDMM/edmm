package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum MediaConvertType {
    JobTemplate(ComponentType.Software_Component),
    Preset(ComponentType.Software_Component),
    Queue(ComponentType.Platform);

    ComponentType componentType;

    MediaConvertType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
