package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ACMPCAType {
    Certificate(ComponentType.Software_Component),
    CertificateAuthority(ComponentType.Software_Component),
    CertificateAuthorityActivation(ComponentType.Software_Component);

    ComponentType componentType;

    ACMPCAType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
