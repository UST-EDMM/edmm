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
package io.github.edmm.plugins.azure.model.resource.network.virtualnetworks.subnets;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;
import org.apache.commons.lang3.tuple.ImmutablePair;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Subnet extends Resource {

    public Subnet( ) {
        super(ResourceTypeEnum.SUBNETS);
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        // the name of the subnet should be set when detecting a new Compute node in the topology
        setName("");
        setProperties(SubnetProperties.builder()
                // address prefix parameter should be set
                .addressPrefix("[parameter()]")
                // id (name) of security group should be set when detecting a new Compute node in toplogy
                .networkSecurityGroup(ImmutablePair.of("id", ""))
                .build());
    }
}
