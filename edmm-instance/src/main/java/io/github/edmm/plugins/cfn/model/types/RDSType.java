package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum RDSType {
    DBCluster(ComponentType.DBaaS),
    DBClusterParameterGroup(ComponentType.Software_Component),
    DBInstance(ComponentType.DBaaS),
    DBParameterGroup(ComponentType.Software_Component),
    DBProxy(ComponentType.Software_Component),
    DBProxyTargetGroup(ComponentType.Software_Component),
    DBSecurityGroup(ComponentType.Software_Component),
    DBSecurityGroupIngress(ComponentType.Software_Component),
    DBSubnetGroup(ComponentType.Software_Component),
    EventSubscription(ComponentType.Software_Component),
    OptionGroup(ComponentType.Software_Component);

    ComponentType componentType;

    RDSType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
