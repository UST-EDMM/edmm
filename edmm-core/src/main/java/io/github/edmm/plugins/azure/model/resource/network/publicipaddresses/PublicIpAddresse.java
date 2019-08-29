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
package io.github.edmm.plugins.azure.model.resource.network.publicipaddresses;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.Parameter;
import io.github.edmm.plugins.azure.model.ParameterTypeEnum;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicIpAddresse extends Resource {

    public PublicIpAddresse() {
        super(ResourceTypeEnum.PUBLIC_IP_ADDRESSES);
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        this.setApiVersion("2019-04-01");
        this.setName("myPublicIP");

        Map<String, String> dnsSettings = new HashMap<>();
        dnsSettings.put("domainNameLabel", "[parameters('vmDnsName')]");
        this.setProperties(PublicIpAddressProperties.builder()
                .publicIpAllocationMethod("Dynamic")
                .dnsSettings(dnsSettings)
                .build()
        );
    }

    @Override
    protected Map<String, Parameter> getRequiredParameters() {
        Map<String, Parameter> params = super.getRequiredParameters();
        params.put("vmDnsName", Parameter.builder().type(ParameterTypeEnum.STRING).build());

        return params;
    }
}
