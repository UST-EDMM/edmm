package io.github.edmm.plugins.azure.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Parameter {
    private ParameterTypeEnum type;
    private String defaultValue;
}
