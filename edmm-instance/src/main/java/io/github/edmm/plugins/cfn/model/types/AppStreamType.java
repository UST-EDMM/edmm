package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum AppStreamType {
    DirectoryConfig(ComponentType.Software_Component),
    Fleet(ComponentType.Compute),
    ImageBuilder(ComponentType.Compute),
    Stack(ComponentType.Compute),
    StackFleetAssociation(ComponentType.Software_Component),
    StackUserAssociation(ComponentType.Software_Component),
    User(ComponentType.Software_Component);

    ComponentType componentType;

    AppStreamType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
