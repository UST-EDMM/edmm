package io.github.edmm.plugins.azure.model.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.edmm.plugins.azure.model.Parameter;
import io.github.edmm.plugins.azure.model.ParameterTypeEnum;
import lombok.Data;

/**
 * A generic Azure resource. It includes the values that are expected to exist in all concrete resource classes.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Resource {
    /**
     * Used to retrieve the full type name of this resource. Set by child types.
     */
    private final ResourceTypeEnum type;
    private final String name;
    /**
     * The api version of this resource. Each resource type has a set of allowed api versions.
     */
    private String apiVersion;
    private String location;
    private List<String> dependsOn;
    private Properties properties;

    public Resource(ResourceTypeEnum type, String name) {
        this.type = type;
        this.name = name;
        setDefaults();
    }

    protected void setDefaults() {
        this.setLocation("[parameters('location')]");
    }

    @JsonIgnore
    public String getFullName() {
        return String.format("%s/%s", type.getTypeName(), name);
    }

    @JsonIgnore
    public Map<String, Parameter> getRequiredParameters() {
        Map<String, Parameter> params = new HashMap<>();
        params.put("location", Parameter.builder()
                .type(ParameterTypeEnum.STRING)
                .defaultValue("[resourceGroup().location]")
                .build());

        return params;
    }

    @JsonIgnore
    public Map<String, String> getRequiredVariables() {
        return new HashMap<>();
    }
}
