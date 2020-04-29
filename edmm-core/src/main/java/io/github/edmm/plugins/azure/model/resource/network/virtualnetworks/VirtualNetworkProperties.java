package io.github.edmm.plugins.azure.model.resource.network.virtualnetworks;

import java.util.List;

import io.github.edmm.plugins.azure.model.resource.Properties;
import io.github.edmm.plugins.azure.model.resource.network.virtualnetworks.subnets.Subnet;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualNetworkProperties extends Properties {
    private AddressSpace addressSpace;
    private List<Subnet> subnets;
}

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class AddressSpace {
    private List<String> addressPrefixes;
}
