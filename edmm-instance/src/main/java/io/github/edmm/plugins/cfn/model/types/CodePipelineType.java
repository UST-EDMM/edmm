package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum CodePipelineType {
    CustomActionType(ComponentType.Software_Component),
    Pipeline(ComponentType.Software_Component),
    Webhook(ComponentType.Software_Component);

    ComponentType componentType;

    CodePipelineType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
