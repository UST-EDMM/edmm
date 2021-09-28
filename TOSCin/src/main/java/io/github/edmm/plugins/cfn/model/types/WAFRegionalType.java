package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum WAFRegionalType {
    ByteMatchSet(ComponentType.Software_Component),
    GeoMatchSet(ComponentType.Software_Component),
    IPSet(ComponentType.Software_Component),
    RateBasedRule(ComponentType.Software_Component),
    RegexPatternSet(ComponentType.Software_Component),
    Rule(ComponentType.Software_Component),
    SizeConstraintSet(ComponentType.Software_Component),
    SqlInjectionMatchSet(ComponentType.Software_Component),
    WebACL(ComponentType.Software_Component),
    WebACLAssociation(ComponentType.Software_Component),
    XssMatchSet(ComponentType.Software_Component);

    ComponentType componentType;

    WAFRegionalType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
