package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum SecretsManagerType {
    ResourcePolicy(ComponentType.Software_Component),
    RotationSchedule(ComponentType.Software_Component),
    Secret(ComponentType.Software_Component),
    SecretTargetAttachment(ComponentType.Software_Component);

    ComponentType componentType;

    SecretsManagerType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
