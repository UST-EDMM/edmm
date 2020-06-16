package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum Route53ResolverType {
    ResolverEndpoint(ComponentType.Software_Component),
    ResolverRule(ComponentType.Software_Component),
    ResolverRuleAssociation(ComponentType.Software_Component);

    ComponentType componentType;

    Route53ResolverType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
