package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ServiceDiscoveryType {
    HttpNamespace(ComponentType.Software_Component),
    Instance(ComponentType.Software_Component),
    PrivateDnsNamespace(ComponentType.Software_Component),
    PublicDnsNamespace(ComponentType.Software_Component),
    Service(ComponentType.Software_Component);

    ComponentType componentType;

    ServiceDiscoveryType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
