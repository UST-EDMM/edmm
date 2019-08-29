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

import com.fasterxml.jackson.annotation.JsonValue;

public enum ParameterTypeEnum {
    STRING("string"),
    SECURE_STRING("securestring"),
    INTEGER("int"),
    BOOLEAN("bool"),
    OBJECT("object"),
    SECURE_OBJECT("secureObject"),
    ARRAY("array");

    private String typeName;
    ParameterTypeEnum(String name) {
        this.typeName = name;
    }

    @JsonValue
    public String getTypeName() {
        return typeName;
    }

}
