package io.github.edmm.plugins.azure.model.resource.network.virtualnetworks;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.Parameter;
import io.github.edmm.plugins.azure.model.ParameterTypeEnum;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;
import io.github.edmm.plugins.azure.model.resource.network.virtualnetworks.subnets.Subnet;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualNetwork extends Resource {

    public VirtualNetwork() {
        super(ResourceTypeEnum.VIRTUAL_NETWORKS, "[variables('vnet_name')]");
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        setApiVersion("2019-04-01");
        AddressSpace addressSpace = AddressSpace
            .builder()
            .addressPrefixes(Collections.singletonList("[parameters('vnetAddressSpace')]"))
            .build();
        Subnet subnet = new Subnet();
        setProperties(VirtualNetworkProperties
            .builder()
            .addressSpace(addressSpace)
            .subnets(Collections.singletonList(subnet))
            .build());
    }

    @JsonIgnore
    public List<Subnet> getSubnets() {
        return ((VirtualNetworkProperties) this.getProperties()).getSubnets();
    }

    @Override
    public Map<String, Parameter> getRequiredParameters() {
        Map<String, Parameter> params = super.getRequiredParameters();
        params.put("vnetAddressSpace", Parameter
            .builder()
            .type(ParameterTypeEnum.STRING)
            .defaultValue("10.0.0.0/16")
            .build());
        getSubnets().forEach(subnet -> params.putAll(subnet.getRequiredParameters()));

        return params;
    }

    @Override
    public Map<String, String> getRequiredVariables() {
        Map<String, String> vars = super.getRequiredVariables();
        vars.put("vnet_name", "MyVNET");
        vars.put("vnet_id", "[resourceId('Microsoft.Network/virtualNetworks',variables('vnet_name'))]");
        getSubnets().forEach(subnet -> vars.putAll(subnet.getRequiredVariables()));

        return vars;
    }
}
