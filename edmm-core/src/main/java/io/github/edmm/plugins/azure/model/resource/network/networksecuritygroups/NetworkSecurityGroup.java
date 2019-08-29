
package io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkSecurityGroup extends Resource {
    public NetworkSecurityGroup(String name) {
        super(ResourceTypeEnum.NETWORK_SECURITY_GROUPS, name);
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        setApiVersion("2019-04-01");
        // todo the set of security rules should be populated while analyzing application topology (one per node with PORT)
        setProperties(NetworkSecurityGroupProperties.builder().securityRules(new ArrayList<>()).build());
    }
}
