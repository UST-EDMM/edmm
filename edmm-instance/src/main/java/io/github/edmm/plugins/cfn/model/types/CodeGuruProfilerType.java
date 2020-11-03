package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum CodeGuruProfilerType {
    ProfilingGroup(ComponentType.Software_Component);

    ComponentType componentType;

    CodeGuruProfilerType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
