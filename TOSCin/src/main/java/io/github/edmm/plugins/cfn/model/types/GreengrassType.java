package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum GreengrassType {
    ConnectorDefinition(ComponentType.Software_Component),
    ConnectorDefinitionVersion(ComponentType.Software_Component),
    CoreDefinition(ComponentType.Software_Component),
    CoreDefinitionVersion(ComponentType.Software_Component),
    DeviceDefinition(ComponentType.Software_Component),
    DeviceDefinitionVersion(ComponentType.Software_Component),
    FunctionDefinition(ComponentType.Software_Component),
    FunctionDefinitionVersion(ComponentType.Software_Component),
    Group(ComponentType.Software_Component),
    GroupVersion(ComponentType.Software_Component),
    LoggerDefinition(ComponentType.Software_Component),
    LoggerDefinitionVersion(ComponentType.Software_Component),
    ResourceDefinition(ComponentType.Software_Component),
    ResourceDefinitionVersion(ComponentType.Software_Component),
    SubscriptionDefinition(ComponentType.Software_Component),
    SubscriptionDefinitionVersion(ComponentType.Software_Component);

    ComponentType componentType;

    GreengrassType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
