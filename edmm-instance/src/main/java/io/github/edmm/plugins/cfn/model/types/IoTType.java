package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum IoTType {
    Certificate(ComponentType.Software_Component),
    Policy(ComponentType.Software_Component),
    PolicyPrincipalAttachment(ComponentType.Software_Component),
    ProvisioningTemplate(ComponentType.Software_Component),
    Thing(ComponentType.Software_Component),
    ThingPrincipalAttachment(ComponentType.Software_Component),
    TopicRule(ComponentType.Software_Component);

    ComponentType componentType;

    IoTType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
