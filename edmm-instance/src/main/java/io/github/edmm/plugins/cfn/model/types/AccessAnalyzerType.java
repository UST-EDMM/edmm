package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum AccessAnalyzerType {
    Analyzer(ComponentType.Software_Component);

    ComponentType componentType;

    AccessAnalyzerType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
