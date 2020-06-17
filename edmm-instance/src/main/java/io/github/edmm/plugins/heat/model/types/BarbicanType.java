package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum BarbicanType {
    CertificateContainer(ComponentType.Software_Component),
    GenericContainer(ComponentType.Compute),
    Order(ComponentType.Software_Component),
    RSAContainer(ComponentType.Software_Component),
    Secret(ComponentType.Software_Component);

    private ComponentType componentType;

    BarbicanType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
