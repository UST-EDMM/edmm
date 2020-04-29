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

    private final String typeName;

    ParameterTypeEnum(String name) {
        this.typeName = name;
    }

    @JsonValue
    public String getTypeName() {
        return typeName;
    }
}
