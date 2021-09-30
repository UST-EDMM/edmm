package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ImageBuilderType {
    Component(ComponentType.Software_Component),
    DistributionConfiguration(ComponentType.Software_Component),
    Image(ComponentType.Software_Component),
    ImagePipeline(ComponentType.Software_Component),
    ImageRecipe(ComponentType.Software_Component),
    InfrastructureConfiguration(ComponentType.Software_Component);

    ComponentType componentType;

    ImageBuilderType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
