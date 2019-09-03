package io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups.securityrules;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.resource.Properties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecurityRuleProperties extends Properties {
    private SecurityRuleProtocolEnum protocol;
    private SecurityRuleDirectionEnum direction;
    private SecurityRuleAccessEnum access;
    private String sourcePortRange;
    private String targetPortRange;
    private String destinationAddressPrefix;
}
