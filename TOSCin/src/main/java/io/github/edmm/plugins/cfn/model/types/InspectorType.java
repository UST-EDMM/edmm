package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum InspectorType {
    AssessmentTarget(ComponentType.Compute),
    AssessmentTemplate(ComponentType.Software_Component),
    ResourceGroup(ComponentType.Software_Component);

    ComponentType componentType;

    InspectorType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
