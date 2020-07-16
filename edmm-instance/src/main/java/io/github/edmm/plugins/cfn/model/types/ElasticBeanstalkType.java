package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ElasticBeanstalkType {
    Application(ComponentType.Software_Component),
    ApplicationVersion(ComponentType.Software_Component),
    ApplicationConfigurationTemplate(ComponentType.Software_Component),
    Environment(ComponentType.Platform);

    ComponentType componentType;

    ElasticBeanstalkType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
