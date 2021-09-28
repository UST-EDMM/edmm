package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum AppMeshType {
    Mesh(ComponentType.Software_Component),
    Route(ComponentType.Software_Component),
    VirtualNode(ComponentType.Compute),
    VirtualRouter(ComponentType.Software_Component),
    VirtualService(ComponentType.Software_Component);

    ComponentType componentType;

    AppMeshType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
