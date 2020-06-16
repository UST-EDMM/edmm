package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum KinesisAnalyticsType {
    Application(ComponentType.Software_Component),
    ApplicationOutput(ComponentType.Software_Component),
    ApplicationReferenceDataSource(ComponentType.DBaaS);

    ComponentType componentType;

    KinesisAnalyticsType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
