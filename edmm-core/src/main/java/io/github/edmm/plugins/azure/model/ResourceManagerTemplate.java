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
package io.github.edmm.plugins.azure.model;


import java.util.List;
import java.util.Map;

import io.github.edmm.plugins.azure.model.resource.Resource;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ResourceManagerTemplate {
    private final String $schema = "https://schema.management.azure.com/schemas/2015-01-01/deploymentTemplate.json#";
    private String contentVersion = "1.0.0.0";
    private Map<String, Parameter> parameters;
    private List<Resource> resources;

}
