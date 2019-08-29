
package io.github.edmm.plugins.azure.model.resource.network.virtualnetworks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.Parameter;
import io.github.edmm.plugins.azure.model.ParameterTypeEnum;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;

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
        setProperties(VirtualNetworkProperties
                .builder()
                .addressSpace(addressSpace)
                // todo the addition of a new subnet should happen when detecting a new Compute node in the topology
                .subnets(new ArrayList<>())
                .build());
    }

    @Override
    protected Map<String, Parameter> getRequiredParameters() {
        Map<String, Parameter> params = super.getRequiredParameters();
        params.put("vnetAddressSpace", Parameter
                .builder()
                .type(ParameterTypeEnum.STRING)
                .defaultValue("10.0.0.0/16")
                .build());

        return params;
    }

    @Override
    protected Map<String, String> getRequiredVariables() {
        Map<String, String> vars = super.getRequiredVariables();
        vars.put("vnet_name", "MyVNET");
        vars.put("vnet_id", "[resourceId('Microsoft.Network/virtualNetworks',variables('vnet_name'))]");
        return vars;
    }
}
