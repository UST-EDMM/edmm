package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum DetectiveType {
    Graph(ComponentType.DBaaS),
    MemberInvitation(ComponentType.Software_Component);

    ComponentType componentType;

    DetectiveType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
