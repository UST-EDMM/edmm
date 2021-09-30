package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum SESType {
    ConfigurationSet(ComponentType.Software_Component),
    ConfigurationSetEventDestination(ComponentType.Software_Component),
    ReceiptFilter(ComponentType.Software_Component),
    ReceiptRule(ComponentType.Software_Component),
    ReceiptRuleSet(ComponentType.Software_Component),
    Template(ComponentType.Software_Component);

    ComponentType componentType;

    SESType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
