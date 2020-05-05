package io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups;

import java.util.List;

import io.github.edmm.plugins.azure.model.resource.Properties;
import io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups.securityrules.SecurityRule;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkSecurityGroupProperties extends Properties {
    private List<SecurityRule> securityRules;
}
