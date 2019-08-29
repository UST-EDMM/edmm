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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.Parameter;
import io.github.edmm.plugins.azure.model.ParameterTypeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A generic Azure resource. It includes the values that are expected to exist in all concrete resource classes.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Resource {
    /**
     * The api version of this resource. Each resource type has a set of allowed api versions.
     */
    private String apiVersion;
    /**
     * Used to retrieve the full type name of this resource. Set by child types.
     */
    private final ResourceTypeEnum type;
    private String name;
    private String location;
    private List<String> dependsOn;
    private Properties properties;

    public Resource(ResourceTypeEnum type) {
        this.type = type;
        setDefaults();
    }

    protected void setDefaults() {
        this.setLocation("[parameters('location')]");
    }
    protected Map<String,Parameter> getRequiredParameters() {
        Map<String, Parameter> params = new HashMap<>();
        params.put("location", Parameter.builder()
                .type(ParameterTypeEnum.STRING)
                .defaultValue("[resourceGroup().location]")
                .build());

        return params;
    }
}
