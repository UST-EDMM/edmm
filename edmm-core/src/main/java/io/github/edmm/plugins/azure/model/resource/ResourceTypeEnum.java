package io.github.edmm.plugins.azure.model.resource;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResourceTypeEnum {
    STORAGE_ACCOUNTS("Microsoft.Storage", "storageAccounts"),
    PUBLIC_IP_ADDRESSES("Microsoft.Network", "publicIPAddresses"),
    VIRTUAL_NETWORKS("Microsoft.Network", "virtualNetworks"),
    SUBNETS("Microsoft.Network", "virtualNetworks/subnets"),
    NETWORK_INTERFACES("Microsoft.Network", "networkInterfaces"),
    NETWORK_SECURITY_GROUPS("Microsoft.Network", "networkSecurityGroups"),
    SECURITY_RULES("Microsoft.Network", "networkSecurityGroups/securityRules"),
    VIRTUAL_MACHINES("Microsoft.Compute", "virtualMachines"),
    VIRTUAL_MACHINE_EXTENSIONS("Microsoft.Compute", "virtualMachines/extensions");

    private final String providerNamespace;
    private final String serviceName;

    ResourceTypeEnum(String providerNamespace, String serviceName) {
        this.providerNamespace = providerNamespace;
        this.serviceName = serviceName;
    }

    @JsonValue
    public String getTypeName() {
        return String.format("%s/%s", providerNamespace, serviceName);
    }
}
