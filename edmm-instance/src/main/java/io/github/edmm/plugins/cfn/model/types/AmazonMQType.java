package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum AmazonMQType {
    Broker(ComponentType.SaaS),
    Configuration(ComponentType.Software_Component),
    ConfigurationAssociation(ComponentType.Software_Component);

    ComponentType componentType;

    AmazonMQType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
