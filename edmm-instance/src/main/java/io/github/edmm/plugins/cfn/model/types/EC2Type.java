package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

// TODO add all other types, these two are just to validate approach
public enum EC2Type {
    Instance(ComponentType.Compute),
    SecurityGroup(ComponentType.Software_Component);

    ComponentType componentType;

    EC2Type(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
