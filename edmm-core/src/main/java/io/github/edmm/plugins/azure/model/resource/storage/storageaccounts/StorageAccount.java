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
package io.github.edmm.plugins.azure.model.resource.storage.storageaccounts;

import java.util.HashMap;
import java.util.Map;

import io.github.edmm.plugins.azure.model.Parameter;
import io.github.edmm.plugins.azure.model.ParameterTypeEnum;
import io.github.edmm.plugins.azure.model.SkuTypeEnum;
import io.github.edmm.plugins.azure.model.resource.Properties;
import io.github.edmm.plugins.azure.model.resource.Resource;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;
import lombok.Data;

/**
 * Default configuration assumes:
 * - a parameter called 'location'
 * - a parameter called 'storageAccountName'
 */
@Data
public class StorageAccount extends Resource {
    private StorageAccountKindEnum kind;
    private SkuTypeEnum sku;

    public StorageAccount() {
        super(ResourceTypeEnum.STORAGE_ACCOUNTS);
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        this.setApiVersion("2019-04-01");
        this.setName("[parameter('storageAccountName')]");
        this.setKind(StorageAccountKindEnum.Storage);
        this.setSku(SkuTypeEnum.Standard_LRS);
        this.setProperties(new Properties() {
        });
    }

    @Override
    protected Map<String, Parameter> getRequiredParameters() {
        Map<String, Parameter> params = super.getRequiredParameters();
        params.put("storageAccountName", Parameter.builder()
                .type(ParameterTypeEnum.STRING)
                .defaultValue("[concat(uniquestring(resourceGroup().id), 'myvmsa')]")
                .build());

        return params;
    }
}
