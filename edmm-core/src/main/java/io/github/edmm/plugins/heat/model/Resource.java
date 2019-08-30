package io.github.edmm.plugins.heat.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Resource {

    @JsonIgnore
    private String name;
    private String type;
    private Map<String, PropertyAssignment> properties;

    public void addPropertyAssignment(String name, PropertyAssignment property) {
        if (properties == null) {
            properties = new HashMap<>();
        }
        properties.put(name, property);
    }
}
