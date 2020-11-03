package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ElastiCacheType {
    CacheCluster(ComponentType.DBaaS),
    ParameterGroup(ComponentType.Software_Component),
    ReplicationGroup(ComponentType.DBaaS),
    SecurityGroup(ComponentType.Software_Component),
    SecurityGroupIngress(ComponentType.Software_Component),
    SubnetGroup(ComponentType.Software_Component);

    ComponentType componentType;

    ElastiCacheType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
