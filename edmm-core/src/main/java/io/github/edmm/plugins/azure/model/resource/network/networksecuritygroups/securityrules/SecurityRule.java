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
package io.github.edmm.plugins.azure.model.resource.network.networksecuritygroups.securityrules;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecurityRule extends Resource {

    public SecurityRule() {
        super(ResourceTypeEnum.SECURITY_RULES);
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        setApiVersion("2019-04-01");
        // generate a random name for the rule
        setName("[uniqueString(utcNow())]");
        setProperties(SecurityRuleProperties.builder()
        .access(SecurityRuleAccessEnum.Allow)
        .destinationAddressPrefix("*")
        .direction(SecurityRuleDirectionEnum.Inbound)
        .protocol(SecurityRuleProtocolEnum.TCP)
        .sourcePortRange("*")
        .build());
    }
}
