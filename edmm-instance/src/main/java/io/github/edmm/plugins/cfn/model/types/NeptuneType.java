package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum NeptuneType {
    DBCluster(ComponentType.DBMS),
    DBClusterParameterGroup(ComponentType.Software_Component),
    DBInstance(ComponentType.DBaaS),
    DBParameterGroup(ComponentType.Software_Component),
    DBSubnetGroup(ComponentType.Software_Component);

    ComponentType componentType;

    NeptuneType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
