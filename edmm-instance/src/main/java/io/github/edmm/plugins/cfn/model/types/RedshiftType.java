package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum RedshiftType {
    Cluster(ComponentType.DBMS),
    ClusterParameterGroup(ComponentType.Software_Component),
    ClusterSecurityGroup(ComponentType.Software_Component),
    ClusterSecurityGroupIngress(ComponentType.Software_Component),
    ClusterSubnetGroup(ComponentType.Software_Component);

    ComponentType componentType;

    RedshiftType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
