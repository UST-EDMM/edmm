package io.github.ust.edmm.model;

import java.util.Objects;
import java.util.Optional;

import io.github.ust.edmm.core.parser.MappingEntity;

public class PropertyDefinition extends ModelEntity {

    private static Attribute<String> TYPE = new Attribute<>("type", String.class);
    private static Attribute<String> VALUE = new Attribute<>("value", String.class);
    private static Attribute<Boolean> REQUIRED = new Attribute<>("required", Boolean.class);
    private static Attribute<String> DEFAULT_VALUE = new Attribute<>("default_value", String.class);

    public PropertyDefinition(MappingEntity entity) {
        super(entity);
    }

    public String getType() {
        String type = get(TYPE);
        if (Objects.isNull(type)) {
            throw new IllegalStateException("PropertyDefinition needs to have a type");
        }
        return type;
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(get(VALUE));
    }

    public Boolean isRequired() {
        Boolean required = get(REQUIRED);
        if (Objects.isNull(required)) {
            return false;
        }
        return required;
    }

    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(get(DEFAULT_VALUE));
    }
}
