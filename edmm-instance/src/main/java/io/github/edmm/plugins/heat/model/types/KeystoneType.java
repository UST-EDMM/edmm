package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum KeystoneType {
    Domain(ComponentType.Software_Component),
    Endpoint(ComponentType.Software_Component),
    Group(ComponentType.Software_Component),
    GroupRoleAssignment(ComponentType.Software_Component),
    Project(ComponentType.Software_Component),
    Region(ComponentType.Software_Component),
    Role(ComponentType.Software_Component),
    Service(ComponentType.Software_Component),
    User(ComponentType.Software_Component),
    UserRoleAssignment(ComponentType.Software_Component);

    private ComponentType componentType;

    KeystoneType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
