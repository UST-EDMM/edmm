package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum IAMType {
    AccessKey(ComponentType.Software_Component),
    Group(ComponentType.Software_Component),
    InstanceProfile(ComponentType.Software_Component),
    ManagedPolicy(ComponentType.Software_Component),
    Policy(ComponentType.Software_Component),
    Role(ComponentType.Software_Component),
    ServiceLinkedRole(ComponentType.Software_Component),
    User(ComponentType.Software_Component),
    UserToGroupAddition(ComponentType.Software_Component);

    ComponentType componentType;

    IAMType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
