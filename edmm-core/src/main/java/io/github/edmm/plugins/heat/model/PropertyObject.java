package io.github.edmm.plugins.heat.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Value;

@Value
public class PropertyObject implements PropertyAssignment {

    @JsonValue
    private Object value;
}
