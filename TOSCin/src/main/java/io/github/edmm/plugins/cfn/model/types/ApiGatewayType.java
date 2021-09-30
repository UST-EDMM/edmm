package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ApiGatewayType {
    Account(ComponentType.Software_Component),
    ApiKey(ComponentType.Software_Component),
    Authorizer(ComponentType.Software_Component),
    BasePathMapping(ComponentType.Software_Component),
    ClientCertificate(ComponentType.Software_Component),
    Deployment(ComponentType.Software_Component),
    DocumentationPart(ComponentType.Software_Component),
    DocumentationVersion(ComponentType.Software_Component),
    DomainName(ComponentType.Software_Component),
    GatewayResponse(ComponentType.Software_Component),
    Method(ComponentType.Software_Component),
    Model(ComponentType.Software_Component),
    RequestValidator(ComponentType.Software_Component),
    Resource(ComponentType.Software_Component),
    RestApi(ComponentType.Software_Component),
    Stage(ComponentType.Software_Component),
    UsagePlan(ComponentType.Software_Component),
    UsagePlanKey(ComponentType.Software_Component),
    VpcLink(ComponentType.Software_Component);

    ComponentType componentType;

    ApiGatewayType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
