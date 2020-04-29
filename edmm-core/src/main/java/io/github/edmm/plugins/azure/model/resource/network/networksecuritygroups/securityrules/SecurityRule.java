package io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups.securityrules;

import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecurityRule extends Resource {

    // setting a name for the security rule should happen when detecting a node that has a PORT attribute
    public SecurityRule(String name) {
        super(ResourceTypeEnum.SECURITY_RULES, name);
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        setApiVersion("2019-04-01");
        setProperties(SecurityRuleProperties.builder()
            .access(SecurityRuleAccessEnum.Allow)
            .destinationAddressPrefix("*")
            .direction(SecurityRuleDirectionEnum.Inbound)
            .protocol(SecurityRuleProtocolEnum.TCP)
            .sourcePortRange("*")
            .build());
    }
}
