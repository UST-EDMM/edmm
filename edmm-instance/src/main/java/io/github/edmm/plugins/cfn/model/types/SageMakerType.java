package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum SageMakerType {
    CodeRepository(ComponentType.Software_Component),
    Endpoint(ComponentType.Software_Component),
    EndpointConfig(ComponentType.Software_Component),
    Model(ComponentType.Software_Component),
    NotebookInstance(ComponentType.Compute),
    NotebookInstanceLifecycleConfig(ComponentType.Software_Component),
    Workteam(ComponentType.Software_Component);

    ComponentType componentType;

    SageMakerType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
