package com.scaleset.cfbuilder.core;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Tag {

    protected ObjectNode node = JsonNodeFactory.instance.objectNode();

    public Tag(String key, String value) {
        node.put("Key", key);
        node.put("Value", value);
    }

    public String getKey() {
        return node.get("Key").textValue();
    }

    public String getValue() {
        return node.get("Value").textValue();
    }

    public ObjectNode toNode() {
        return node;
    }
}
