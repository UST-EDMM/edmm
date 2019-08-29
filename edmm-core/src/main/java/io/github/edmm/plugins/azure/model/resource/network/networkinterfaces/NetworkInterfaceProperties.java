package io.github.edmm.plugins.azure.model.resource.network.networkinterfaces;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.resource.Properties;
import io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups.NetworkSecurityGroup;
import io.github.edmm.plugins.azure.model.resource.network.publicipaddresses.PublicIpAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkInterfaceProperties extends Properties {
    private NetworkSecurityGroup networkSecurityGroup;
    private IpConfiguration ipConfiguration;
}

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class IpConfiguration {
    private String name;
    private IpConfigurationProperties properties;
}

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class IpConfigurationProperties {
    private String privateIpAllocationMethod;
    private PublicIpAddress publicIpAddress;
    private SubnetReference subnet;
}

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class SubnetReference {
    private String id;
}
