package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum DocDBType {
    DBCluster(ComponentType.DBMS),
    DBClusterParameterGroup(ComponentType.Software_Component),
    DBInstance(ComponentType.DBaaS),
    DBSubnetGroup(ComponentType.Software_Component);

    ComponentType componentType;

    DocDBType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
