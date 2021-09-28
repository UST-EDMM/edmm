package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ElasticLoadBalancingV2Type {
    Listener(ComponentType.Software_Component),
    ListenerCertificate(ComponentType.Software_Component),
    ListenerRule(ComponentType.Software_Component),
    LoadBalancer(ComponentType.Compute),
    TargetGroup(ComponentType.Compute);

    ComponentType componentType;

    ElasticLoadBalancingV2Type(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
