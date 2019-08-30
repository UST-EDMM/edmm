package io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups.securityrules;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SecurityRuleProtocolEnum {
    TCP("Tcp"),
    UDP("Udp"),
    ICMP("Icmp"),
    ESP("Esp"),
    ALL_PROTOCOLS("*");

    private String protocolName;

    SecurityRuleProtocolEnum(String name) {
        this.protocolName = name;
    }

    @JsonValue
    public String getProtocolName() {
        return this.protocolName;
    }
}
