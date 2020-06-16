package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum S3Type {
    AccessPoint(ComponentType.Software_Component),
    Bucket(ComponentType.DBaaS),
    BucketPolicy(ComponentType.Software_Component);

    ComponentType componentType;

    S3Type(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
