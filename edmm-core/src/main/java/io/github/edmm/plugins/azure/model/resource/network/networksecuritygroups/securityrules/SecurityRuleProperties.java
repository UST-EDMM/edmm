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
import io.github.edmm.plugins.azure.model.resource.Properties;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecurityRuleProperties extends Properties {
    private SecurityRuleProtocolEnum protocol;
    private SecurityRuleDirectionEnum direction;
    private SecurityRuleAccessEnum access;
    private String sourcePortRange;
    private String targetPortRange;
    private String destinationAddressPrefix;
}
