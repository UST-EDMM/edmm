package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum HeatType {
    AccessPolicy(ComponentType.Software_Component),
    AutoScalingGroup(ComponentType.Compute),
    CloudConfig(ComponentType.Software_Component),
    Delay(ComponentType.Software_Component),
    DeployedServer(ComponentType.Compute),
    InstanceGroup(ComponentType.Compute),
    MultipartMime(ComponentType.Software_Component),
    None(ComponentType.Software_Component),
    RandomString(ComponentType.Software_Component),
    ResourceChain(ComponentType.Compute),
    ResourceGroup(ComponentType.Compute),
    ScalingPolicy(ComponentType.Software_Component),
    SoftwareComponent(ComponentType.Software_Component),
    SoftwareConfig(ComponentType.Software_Component),
    SoftwareDeployment(ComponentType.Software_Component),
    SoftwareDeploymentGroup(ComponentType.Software_Component),
    Stack(ComponentType.Compute),
    StructuredConfig(ComponentType.Software_Component),
    StructuredDeployment(ComponentType.Software_Component),
    StructuredDeploymentGroup(ComponentType.Software_Component),
    SwiftSignal(ComponentType.Software_Component),
    SwiftSignalHandle(ComponentType.Software_Component),
    TestResource(ComponentType.Software_Component),
    UpdateWaitConditionHandle(ComponentType.Software_Component),
    Value(ComponentType.Software_Component),
    WaitCondition(ComponentType.Software_Component),
    WaitConditionHandle(ComponentType.Software_Component);

    private ComponentType componentType;

    HeatType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
