package io.github.edmm.model.parameters;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InputParameter {

    private final String key;
    private final ParameterType type;
    private final String description;
    private final boolean required;
    private final String defaultValue;
    private String value;

    public InputParameter(String key, ParameterType type, String description, boolean required) {
        this(key, type, description, required, null);
    }

    public InputParameter(String key, ParameterType type, String description, boolean required, String defaultValue) {
        this(key, type, description, required, defaultValue, defaultValue);
    }

    @JsonCreator
    public InputParameter(@JsonProperty("key") String key, @JsonProperty("type") ParameterType type,
                          @JsonProperty("description") String description, @JsonProperty("required") boolean required,
                          @JsonProperty("defaultValue") String defaultValue, @JsonProperty("value") String value) {
        this.key = key;
        this.type = type;
        this.description = description;
        this.required = required;
        this.defaultValue = defaultValue;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputParameter that = (InputParameter) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
