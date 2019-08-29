package io.github.edmm.plugins.azure.model.resource.network.networkinterfaces;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;
import io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups.NetworkSecurityGroup;
import io.github.edmm.plugins.azure.model.resource.network.publicipaddresses.PublicIpAddress;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkInterface extends Resource {

    // todo setting the name of a network interface happens when detecting a Compute node in the topology
    public NetworkInterface(String name) {
        super(ResourceTypeEnum.NETWORK_INTERFACES, name);
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        setApiVersion("2019-04-01");
        final NetworkSecurityGroup securityGroup = new NetworkSecurityGroup(String.format("%s_securityGroup", this.getName()));
        final PublicIpAddress publicIpAddress = new PublicIpAddress(String.format("%s_publicIpAddress", this.getName()));

        setProperties(NetworkInterfaceProperties
                .builder()
                .networkSecurityGroup(securityGroup)
                .ipConfiguration(IpConfiguration
                        .builder()
                        .name(String.format("%s_ipConfiguration", this.getName()))
                        .properties(IpConfigurationProperties
                                .builder()
                                .privateIpAllocationMethod("Dynamic")
                                .publicIpAddress(publicIpAddress)
                                .subnet(SubnetReference
                                        .builder()
                                        .id("[variables('subnet_id')]")
                                        .build())
                                .build())
                        .build())
                .build());
    }
}
