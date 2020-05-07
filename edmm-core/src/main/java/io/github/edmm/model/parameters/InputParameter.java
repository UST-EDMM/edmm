package io.github.edmm.model.parameters;

import java.util.Optional;

import lombok.Data;

@Data
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
        this.key = key;
        this.type = type;
        this.description = description;
        this.required = required;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }
}
