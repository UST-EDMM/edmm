package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum AppSyncType {
    ApiCache(ComponentType.Software_Component),
    ApiKey(ComponentType.Software_Component),
    DataSource(ComponentType.DBaaS),
    FunctionConfiguration(ComponentType.Software_Component),
    GraphQLApi(ComponentType.Software_Component),
    GraphQLSchema(ComponentType.Software_Component),
    Resolver(ComponentType.Software_Component);

    ComponentType componentType;

    AppSyncType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
