package io.github.edmm.plugins.heat.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Value;

@Value
public class PropertyValue implements PropertyAssignment {

    @JsonValue
    private String value;
}
