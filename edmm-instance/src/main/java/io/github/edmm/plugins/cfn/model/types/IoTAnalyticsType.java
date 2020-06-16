package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum IoTAnalyticsType {
    Channel(ComponentType.Software_Component),
    Dataset(ComponentType.Software_Component),
    Datastore(ComponentType.DBaaS),
    Pipeline(ComponentType.Software_Component);

    ComponentType componentType;

    IoTAnalyticsType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
