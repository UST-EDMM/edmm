package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ConfigType {
    AggregationAuthorization(ComponentType.Software_Component),
    ConfigRule(ComponentType.Software_Component),
    ConfigurationAggregator(ComponentType.Software_Component),
    ConfigurationRecorder(ComponentType.Software_Component),
    ConformancePack(ComponentType.Software_Component),
    DeliveryChannel(ComponentType.Software_Component),
    OrganizationConfigRule(ComponentType.Software_Component),
    OrganizationConformancePack(ComponentType.Web_Server),
    RemediationConfiguration(ComponentType.Software_Component);

    ComponentType componentType;

    ConfigType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
