package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum OctaviaType {
    Flavor(ComponentType.Software_Component),
    FlavorProfile(ComponentType.Software_Component),
    HealthMonitor(ComponentType.Software_Component),
    L7Policy(ComponentType.Software_Component),
    L7Rule(ComponentType.Software_Component),
    Listener(ComponentType.Software_Component),
    LoadBalancer(ComponentType.Compute),
    Pool(ComponentType.Compute),
    PoolMember(ComponentType.Compute),
    Quota(ComponentType.Software_Component);

    private ComponentType componentType;

    OctaviaType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
