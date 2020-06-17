package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum LBaaSType {
    HealthMonitor(ComponentType.Software_Component),
    L7Policy(ComponentType.Software_Component),
    L7Rule(ComponentType.Software_Component),
    Listener(ComponentType.Software_Component),
    LoadBalancer(ComponentType.Compute),
    Pool(ComponentType.Compute),
    PoolMember(ComponentType.Compute),
    IKEPolicy(ComponentType.Software_Component),
    IPsecPolicy(ComponentType.Software_Component),
    IPsecSiteConnection(ComponentType.Software_Component),
    L2Gateway(ComponentType.Software_Component),
    L2GatewayConnection(ComponentType.Software_Component),
    Notification(ComponentType.Compute);

    private ComponentType componentType;

    LBaaSType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
