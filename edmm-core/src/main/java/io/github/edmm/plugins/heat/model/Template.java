package io.github.edmm.plugins.heat.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import io.github.edmm.core.transformation.TransformationException;
import lombok.Data;

@Data
public class Template {

    private static ObjectMapper mapper;

    @JsonIgnore
    private String name;
    private String heatTemplateVersion = "2016-10-14";
    private String description;
    private Map<String, Parameter> parameters;
    private Map<String, Resource> resources;

    public void addParameter(Parameter parameter) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(parameter.getName(), parameter);
    }

    public void addResource(Resource resource) {
        if (resources == null) {
            resources = new HashMap<>();
        }
        resources.put(resource.getName(), resource);
    }

    @JsonIgnore
    public String toYaml() {
        if (mapper == null) {
            YAMLFactory f = new YAMLFactory();
            f.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
            f.enable(YAMLGenerator.Feature.SPLIT_LINES);
            f.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
            mapper = new ObjectMapper(f);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        }
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new TransformationException(e);
        }
    }

    @Override
    public String toString() {
        return toYaml();
    }
}
