package com.scaleset.cfbuilder.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder( {"Description", "Value"})
public class Output {

    @JsonIgnore
    private String id;

    private Object value;

    private String description;

    public Output(String id, Object value, String description) {
        this.id = id;
        this.value = value;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public Object getValue() {
        return value;
    }
}
