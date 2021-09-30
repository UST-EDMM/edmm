package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ECSType {
    Cluster(ComponentType.Compute),
    PrimaryTaskSet(ComponentType.Software_Component),
    Service(ComponentType.Software_Component),
    TaskDefinition(ComponentType.Software_Component),
    TaskSet(ComponentType.Software_Component);

    ComponentType componentType;

    ECSType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
