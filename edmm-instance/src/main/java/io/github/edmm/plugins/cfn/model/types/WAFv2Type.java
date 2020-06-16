package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum WAFv2Type {
    IPSet(ComponentType.Software_Component),
    RegexPatternSet(ComponentType.Software_Component),
    RuleGroup(ComponentType.Software_Component),
    WebACL(ComponentType.Software_Component),
    WebACLAssociation(ComponentType.Software_Component);

    ComponentType componentType;

    WAFv2Type(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
