package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum CodeBuildType {
    Project(ComponentType.Software_Component),
    ReportGroup(ComponentType.Software_Component),
    SourceCredential(ComponentType.Software_Component);

    ComponentType componentType;

    CodeBuildType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
