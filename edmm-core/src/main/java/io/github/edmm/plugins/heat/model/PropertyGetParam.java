package io.github.edmm.plugins.heat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class PropertyGetParam implements PropertyAssignment {

    @JsonProperty("get_param")
    private String name;
}
