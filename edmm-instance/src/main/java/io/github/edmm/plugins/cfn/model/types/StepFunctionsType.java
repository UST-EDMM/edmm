package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum StepFunctionsType {
    Activity(ComponentType.Software_Component),
    StateMachine(ComponentType.Compute);

    ComponentType componentType;

    StepFunctionsType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
