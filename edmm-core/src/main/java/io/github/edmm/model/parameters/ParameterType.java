package io.github.edmm.model.parameters;

import io.github.edmm.model.parameters.validators.BooleanValidator;
import io.github.edmm.model.parameters.validators.FloatValidator;
import io.github.edmm.model.parameters.validators.IntegerValidator;
import io.github.edmm.model.parameters.validators.StringValidator;
import io.github.edmm.model.parameters.validators.ValueValidator;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ParameterType {

    NAME("name", String.class, new StringValidator("([a-z])+((-)*([a-z]))*")),
    TEXT("text", String.class, new StringValidator()),
    INTEGER("integer", Integer.class, new IntegerValidator(false)),
    FLOAT("float", Double.class, new FloatValidator()),
    BOOLEAN("boolean", Boolean.class, new BooleanValidator()),
    SECRET("secret", String.class, new StringValidator());

    private final String name;
    private final Class<?> clazz;
    private final ValueValidator validator;

    ParameterType(String name, Class<?> clazz, ValueValidator validator) {
        this.validator = validator;
        this.name = name;
        this.clazz = clazz;
    }

    public boolean validate(String input) {
        return validator.isValid(input);
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
