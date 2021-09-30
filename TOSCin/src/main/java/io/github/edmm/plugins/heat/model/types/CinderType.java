package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum CinderType {
    EncryptedVolumeType(ComponentType.Software_Component),
    QoSAssociation(ComponentType.Software_Component),
    QoSSpecs(ComponentType.Software_Component),
    Quota(ComponentType.Software_Component),
    Volume(ComponentType.Software_Component),
    VolumeAttachment(ComponentType.Software_Component),
    VolumeType(ComponentType.Software_Component);

    private ComponentType componentType;

    CinderType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
