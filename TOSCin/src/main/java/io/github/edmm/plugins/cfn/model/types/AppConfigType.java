package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum AppConfigType {
    Application(ComponentType.Software_Component),
    ConfigurationProfile(ComponentType.Software_Component),
    Deployment(ComponentType.Software_Component),
    DeploymentStrategy(ComponentType.Software_Component),
    Environment(ComponentType.Software_Component);

    ComponentType componentType;

    AppConfigType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
