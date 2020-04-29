package io.github.edmm.plugins.azure.model.resource.network.networkinterfaces;

import java.util.Collections;
import java.util.Map;

import io.github.edmm.plugins.azure.model.Parameter;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;
import io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups.NetworkSecurityGroup;
import io.github.edmm.plugins.azure.model.resource.network.publicipaddresses.PublicIpAddress;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkInterface extends Resource {

    // setting the name of a network interface happens when detecting a Compute node in the topology
    public NetworkInterface(String name, String vmName) {
        super(ResourceTypeEnum.NETWORK_INTERFACES, name);
        // the following line cannot be done in the setDefaults method since vmName is not visible there !
        ((NetworkInterfaceProperties) this.getProperties()).getDnsSettings().setInternalDnsNameLabel(vmName);
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
            // this ensures internal communications between vms in the same virtual network to succeed using host names
            .dnsSettings(DnsSettings
                .builder()
                // the magical server name that "switches to azure provided DNS resolution"
                // see https://docs.microsoft.com/en-us/azure/templates/microsoft.network/2019-04-01/networkinterfaces#networkinterfacednssettings-object
                .dnsServers(Collections.singletonList("AzureProvidedDNS"))
                .build())
            .build());
    }

    @JsonIgnore
    public NetworkSecurityGroup getNetworkSecurityGroup() {
        return ((NetworkInterfaceProperties) this.getProperties()).getNetworkSecurityGroup();
    }

    @JsonIgnore
    public PublicIpAddress getPublicIpAddress() {
        return ((NetworkInterfaceProperties) this.getProperties()).getIpConfiguration().getProperties().getPublicIpAddress();
    }

    @JsonIgnore
    public String getInternalDnsNameLabel() {
        return ((NetworkInterfaceProperties) this.getProperties()).getDnsSettings().getInternalDnsNameLabel();
    }

    @Override
    public Map<String, Parameter> getRequiredParameters() {
        Map<String, Parameter> params = super.getRequiredParameters();
        params.putAll(getNetworkSecurityGroup().getRequiredParameters());
        params.putAll(getPublicIpAddress().getRequiredParameters());

        return params;
    }

    @Override
    public Map<String, String> getRequiredVariables() {
        Map<String, String> vars = super.getRequiredVariables();
        vars.putAll(getNetworkSecurityGroup().getRequiredVariables());
        vars.putAll(getPublicIpAddress().getRequiredVariables());

        return vars;
    }
}
