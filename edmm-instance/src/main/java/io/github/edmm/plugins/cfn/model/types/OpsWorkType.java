package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum OpsWorkType {
    App(ComponentType.Software_Component),
    ElasticLoadBalancerAttachment(ComponentType.Compute),
    Instance(ComponentType.Compute),
    Layer(ComponentType.Software_Component),
    Stack(ComponentType.Compute),
    UserProfile(ComponentType.Software_Component),
    Volume(ComponentType.Software_Component);

    ComponentType componentType;

    OpsWorkType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
