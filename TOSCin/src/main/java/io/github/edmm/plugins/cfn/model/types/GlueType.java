package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum GlueType {
    Classifier(ComponentType.Software_Component),
    Connection(ComponentType.Software_Component),
    Crawler(ComponentType.Software_Component),
    Database(ComponentType.DBaaS),
    DataCatalogEncryptionSettings(ComponentType.Software_Component),
    DevEndpoint(ComponentType.Software_Component),
    Job(ComponentType.Software_Component),
    MLTransform(ComponentType.Software_Component),
    Partition(ComponentType.DBaaS),
    SecurityConfiguration(ComponentType.Software_Component),
    Table(ComponentType.DBaaS),
    Trigger(ComponentType.Software_Component),
    Workflow(ComponentType.Software_Component);

    ComponentType componentType;

    GlueType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
