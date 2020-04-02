package io.github.edmm.plugins.azure.model.resource.network.virtualnetworks.subnets;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.Parameter;
import io.github.edmm.plugins.azure.model.ParameterTypeEnum;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Subnet extends Resource {

    public Subnet() {
        super(ResourceTypeEnum.SUBNETS, "Subnet-1");
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        setProperties(SubnetProperties.builder()
            .addressPrefix(("[parameters('subnet_addressPrefix')]"))
            .build());
    }

    @Override
    public Map<String, Parameter> getRequiredParameters() {
        Map<String, Parameter> params = super.getRequiredParameters();
        params.put("subnet_addressPrefix", Parameter.builder().type(ParameterTypeEnum.STRING).defaultValue("10.0.0.0/24").build());

        return params;
    }

    @Override
    public Map<String, String> getRequiredVariables() {
        Map<String, String> vars = super.getRequiredVariables();
        vars.put("subnet_name", "[variables('subnet_name')]");
        vars.put("subnet_id", "[concat(variables('vnet_id'),'/subnets/',variables('subnet_name'))]");

        return vars;
    }
}
