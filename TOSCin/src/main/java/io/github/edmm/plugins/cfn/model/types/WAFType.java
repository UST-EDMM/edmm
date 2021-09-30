package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum WAFType {
    ByteMatchSet(ComponentType.Software_Component),
    IPSet(ComponentType.Software_Component),
    Rule(ComponentType.Software_Component),
    SizeConstraintSet(ComponentType.Software_Component),
    SqlInjectionMatchSet(ComponentType.Software_Component),
    WebACL(ComponentType.Software_Component),
    XssMatchSet(ComponentType.Software_Component);

    ComponentType componentType;

    WAFType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
