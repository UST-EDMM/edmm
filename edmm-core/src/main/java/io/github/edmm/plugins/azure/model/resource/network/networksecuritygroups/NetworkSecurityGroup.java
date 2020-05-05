package io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.edmm.plugins.azure.model.Parameter;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;
import io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups.securityrules.SecurityRule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

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

    @JsonIgnore
    public List<SecurityRule> getSecurityRules() {
        return ((NetworkSecurityGroupProperties) this.getProperties()).getSecurityRules();
    }

    @Override
    public Map<String, String> getRequiredVariables() {
        Map<String, String> vars = super.getRequiredVariables();
        List<SecurityRule> securityRules = this.getSecurityRules();
        securityRules.forEach(securityRule -> vars.putAll(securityRule.getRequiredVariables()));

        return vars;
    }

    @Override
    public Map<String, Parameter> getRequiredParameters() {
        Map<String, Parameter> params = super.getRequiredParameters();
        List<SecurityRule> securityRules = this.getSecurityRules();
        securityRules.forEach(securityRule -> params.putAll(securityRule.getRequiredParameters()));

        return params;
    }
}
