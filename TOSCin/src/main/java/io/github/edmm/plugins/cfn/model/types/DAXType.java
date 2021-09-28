package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum DAXType {
    Cluster(ComponentType.DBaaS),
    ParameterGroup(ComponentType.Software_Component),
    SubnetGroup(ComponentType.Software_Component);

    ComponentType componentType;

    DAXType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
