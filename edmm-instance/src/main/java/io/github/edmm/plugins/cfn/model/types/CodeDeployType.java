package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum CodeDeployType {
    Application(ComponentType.Software_Component),
    DeploymentConfig(ComponentType.Software_Component),
    DeploymentGroup(ComponentType.Software_Component);

    ComponentType componentType;

    CodeDeployType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
