package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum CloudFrontType {
    CloudFrontOriginAccessIdentity(ComponentType.Software_Component),
    Distribution(ComponentType.Software_Component),
    StreamingDistribution(ComponentType.Compute);

    ComponentType componentType;

    CloudFrontType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
