package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum CognitoType {
    IdentityPool(ComponentType.Software_Component),
    IdentityPoolRoleAttachment(ComponentType.Software_Component),
    UserPool(ComponentType.Software_Component),
    UserPoolClient(ComponentType.Software_Component),
    UserPoolDomain(ComponentType.Software_Component),
    UserPoolGroup(ComponentType.Software_Component),
    UserPoolIdentityProvider(ComponentType.Software_Component),
    UserPoolResourceServer(ComponentType.Web_Server),
    UserPoolRiskConfigurationAttachment(ComponentType.Software_Component),
    UserPoolUICustomizationAttachment(ComponentType.Software_Component),
    UserPoolUser(ComponentType.Software_Component),
    UserPoolUserToGroupAttachment(ComponentType.Software_Component);

    ComponentType componentType;

    CognitoType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
