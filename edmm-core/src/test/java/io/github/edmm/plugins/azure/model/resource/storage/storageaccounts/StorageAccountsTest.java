package io.github.edmm.plugins.azure.model.resource.storage.storageaccounts;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.github.edmm.core.plugin.JsonHelper;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

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

public class StorageAccountsTest {
    private static final Logger logger = LoggerFactory.getLogger(StorageAccountsTest.class);

    @Test
    public void testSerialization() throws IOException {
        StorageAccount defaultObject = new StorageAccount();
        String serializedObj = JsonHelper.serializeObj(defaultObject);
        logger.debug(serializedObj);
        ClassPathResource expectedResource = new ClassPathResource("azure/storageAccounts.json");
        String expected = FileUtils.readFileToString(expectedResource.getFile(), StandardCharsets.UTF_8);
        Assert.assertEquals(expected.trim(), serializedObj.trim());
    }
}