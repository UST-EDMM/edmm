package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum SaharaType {
    Cluster(ComponentType.Compute),
    ClusterTemplate(ComponentType.Software_Component),
    DataSource(ComponentType.Database),
    ImageRegistry(ComponentType.Software_Component),
    Job(ComponentType.Software_Component),
    JobBinary(ComponentType.Software_Component),
    NodeGroupTemplate(ComponentType.Compute);

    private ComponentType componentType;

    SaharaType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
