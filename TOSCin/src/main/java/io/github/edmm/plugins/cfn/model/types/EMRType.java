package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum EMRType {
    Cluster(ComponentType.Compute),
    InstanceFleetConfig(ComponentType.Software_Component),
    InstanceGroupConfig(ComponentType.Software_Component),
    SecurityConfiguration(ComponentType.Compute),
    Step(ComponentType.Compute);

    ComponentType componentType;

    EMRType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
