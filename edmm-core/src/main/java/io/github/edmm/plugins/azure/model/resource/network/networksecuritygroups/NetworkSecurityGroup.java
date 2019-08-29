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
package io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups;

import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.Parameter;
import io.github.edmm.plugins.azure.model.ParameterTypeEnum;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;
import io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups.securityrules.SecurityRule;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkSecurityGroup extends Resource {
    public NetworkSecurityGroup() {
        super(ResourceTypeEnum.NETWORK_SECURITY_GROUPS);
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        setApiVersion("2019-04-01");
        // the name of the security group should be set when detecting a new Compute node in the topology
        setName("");
        // the set of security rules should be populated while analyzing application topology
        setProperties(NetworkSecurityGroupProperties.builder().securityRules(new ArrayList<>()).build());
    }

    @Override
    protected Map<String, Parameter> getRequiredParameters() {
        Map<String, Parameter> params = super.getRequiredParameters();
        params.put("networkSG", Parameter.builder().type(ParameterTypeEnum.STRING).build());

        return params;
    }
}
