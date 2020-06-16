package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum CodeStarType {
    GitHubRepository(ComponentType.Software_Component);

    ComponentType componentType;

    CodeStarType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
