package io.github.edmm.plugins.heat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class PropertyGetResource implements PropertyAssignment {

    @JsonProperty("get_resource")
    private String name;
}
