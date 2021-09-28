package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum ServiceCatalogType {
    AcceptedPortfolioShare(ComponentType.Software_Component),
    CloudFormationProduct(ComponentType.Software_Component),
    CloudFormationProvisionedProduct(ComponentType.Software_Component),
    LaunchNotificationConstraint(ComponentType.Software_Component),
    LaunchRoleConstraint(ComponentType.Software_Component),
    LaunchTemplateConstraint(ComponentType.Software_Component),
    Portfolio(ComponentType.Software_Component),
    PortfolioPrincipalAssociation(ComponentType.Software_Component),
    PortfolioProductAssociation(ComponentType.Software_Component),
    PortfolioShare(ComponentType.Software_Component),
    ResourceUpdateConstraint(ComponentType.Software_Component),
    StackSetConstraint(ComponentType.Software_Component),
    TagOption(ComponentType.Software_Component),
    TagOptionAssociation(ComponentType.Software_Component);

    ComponentType componentType;

    ServiceCatalogType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
