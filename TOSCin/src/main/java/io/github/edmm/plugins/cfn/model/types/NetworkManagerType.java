package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum NetworkManagerType {
    CustomerGatewayAssociation(ComponentType.DBMS),
    Device(ComponentType.Compute),
    GlobalNetwork(ComponentType.Software_Component),
    Link(ComponentType.Software_Component),
    LinkAssociation(ComponentType.Software_Component),
    Site(ComponentType.Software_Component),
    TransitGatewayRegistration(ComponentType.Software_Component);

    ComponentType componentType;

    NetworkManagerType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
