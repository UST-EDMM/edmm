package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum CertificateManagerType {
    Certificate(ComponentType.Software_Component);

    ComponentType componentType;

    CertificateManagerType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
