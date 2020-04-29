package io.github.edmm.plugins.azure.model.resource.network.publicipaddresses;

import java.util.HashMap;
import java.util.Map;

import io.github.edmm.plugins.azure.model.Parameter;
import io.github.edmm.plugins.azure.model.ParameterTypeEnum;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicIpAddress extends Resource {

    public PublicIpAddress(String name) {
        super(ResourceTypeEnum.PUBLIC_IP_ADDRESSES, name);
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        this.setApiVersion("2019-04-01");

        Map<String, String> dnsSettings = new HashMap<>();
        dnsSettings.put("domainNameLabel", "[parameters('DnsName')]");
        this.setProperties(PublicIpAddressProperties.builder()
            .publicIpAllocationMethod("Dynamic")
            .dnsSettings(dnsSettings)
            .build()
        );
    }

    @Override
    public Map<String, Parameter> getRequiredParameters() {
        Map<String, Parameter> params = super.getRequiredParameters();
        params.put("DnsName", Parameter.builder().type(ParameterTypeEnum.STRING).build());

        return params;
    }
}
