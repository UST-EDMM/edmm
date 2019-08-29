/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
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

    private String providerNamespace;
    private String serviceName;
    ResourceTypeEnum(String providerNamespace, String serviceName) {
        this.providerNamespace = providerNamespace;
        this.serviceName = serviceName;
    }

    @JsonValue
    public String getTypeName() {
        return String.format("%s/%s", providerNamespace, serviceName);
    }

}
