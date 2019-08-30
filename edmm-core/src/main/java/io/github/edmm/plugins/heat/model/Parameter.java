package io.github.edmm.plugins.heat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Parameter {

    @JsonIgnore
    private String name;
    private String type;
    private String label;
    private String description;

    @JsonProperty("default")
    private String defaultValue;
}
