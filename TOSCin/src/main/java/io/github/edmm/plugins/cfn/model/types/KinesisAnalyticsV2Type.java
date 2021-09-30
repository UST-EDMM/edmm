package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum KinesisAnalyticsV2Type {
    Application(ComponentType.Software_Component),
    ApplicationCloudWatchLoggingOption(ComponentType.Software_Component),
    ApplicationOutput(ComponentType.Software_Component),
    ApplicationReferenceDataSource(ComponentType.DBaaS);

    ComponentType componentType;

    KinesisAnalyticsV2Type(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
