package io.github.edmm.plugins.heat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class PropertyGetFile implements PropertyAssignment {

    @JsonProperty("get_file")
    private String name;
}
