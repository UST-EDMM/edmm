package com.scaleset.cfbuilder.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

@JsonPropertyOrder( {"Type", "Metadata", "Properties"})
public interface Resource extends Referenceable {

    @JsonIgnore
    String getId();

    @JsonProperty("Type")
    String getType();

    @JsonProperty("Properties")
    JsonNode getProperties();

    @JsonProperty("Metadata")
    JsonNode getMetadata();

    default Fn fnGetAtt(String attributeName) {
        return new Fn("GetAtt", getId(), attributeName);
    }
}
