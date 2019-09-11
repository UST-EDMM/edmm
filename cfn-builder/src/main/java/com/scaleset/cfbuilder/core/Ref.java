package com.scaleset.cfbuilder.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Ref {

    private ObjectNode node = JsonNodeFactory.instance.objectNode();

    public static Ref ref(String logicalName) {
        return new Ref(logicalName);
    }

    public Ref(String logicalName) {
        setLogicalName(logicalName);
    }

    @JsonProperty("Ref")
    public String getLogicalName() {
        return node.get("Ref").asText();
    }

    public void setLogicalName(String logicalName) {
        node.put("Ref", logicalName);
    }

    protected JsonNode toNode() {
        return node;
    }
}
