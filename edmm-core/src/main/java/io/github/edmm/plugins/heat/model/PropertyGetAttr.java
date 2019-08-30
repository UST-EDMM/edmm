package io.github.edmm.plugins.heat.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class PropertyGetAttr implements PropertyAssignment {

    @JsonProperty("get_attr")
    private List<Object> values;
}
