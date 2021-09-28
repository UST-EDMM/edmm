package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum CassandraType {
    Keyspace(ComponentType.Software_Component),
    Table(ComponentType.DBaaS);

    ComponentType componentType;

    CassandraType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
