package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ApiGatewayV2Type {
    Api(ComponentType.Software_Component),
    ApiGatewayManagedOverrides(ComponentType.Software_Component),
    ApiMapping(ComponentType.Software_Component),
    Authorizer(ComponentType.Software_Component),
    Deployment(ComponentType.Software_Component),
    DomainName(ComponentType.Software_Component),
    Integration(ComponentType.Software_Component),
    IntegrationResponse(ComponentType.Software_Component),
    Model(ComponentType.Software_Component),
    Route(ComponentType.Software_Component),
    RouteResponse(ComponentType.Software_Component),
    Stage(ComponentType.Software_Component),
    VpcLink(ComponentType.Software_Component);

    ComponentType componentType;

    ApiGatewayV2Type(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
