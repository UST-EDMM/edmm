package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum LambdaType {
    Alias(ComponentType.Software_Component),
    EventInvokeConfig(ComponentType.Software_Component),
    EventSourceMapping(ComponentType.Software_Component),
    Function(ComponentType.PaaS),
    LayerVersion(ComponentType.Software_Component),
    LayerVersionPermission(ComponentType.Software_Component),
    Permission(ComponentType.Software_Component),
    Version(ComponentType.Software_Component);

    ComponentType componentType;

    LambdaType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
