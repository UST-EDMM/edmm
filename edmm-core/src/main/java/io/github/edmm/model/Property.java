package io.github.edmm.model;

import java.util.Objects;
import java.util.Optional;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.support.DescribableElement;
import lombok.ToString;

@ToString
public class Property extends DescribableElement {

    public static Attribute<String> TYPE = new Attribute<>("type", String.class);
    public static Attribute<Boolean> REQUIRED = new Attribute<>("required", Boolean.class);
    public static Attribute<String> DEFAULT_VALUE = new Attribute<>("default_value", String.class);
    public static Attribute<String> VALUE = new Attribute<>("value", String.class);

    private final MappingEntity componentEntity;

    public Property(MappingEntity propertyDefinition, MappingEntity componentEntity) {
        super(propertyDefinition);
        this.componentEntity = componentEntity;
    }

    public String getType() {
        String type = resolveByEntityChain(TYPE);
        if (Objects.isNull(type)) {
            throw new IllegalStateException("Property needs to have a type");
        }
        return type;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(resolveByEntityChain(DESCRIPTION));
    }

    public Boolean isRequired() {
        Boolean required = resolveByEntityChain(REQUIRED);
        if (Objects.isNull(required)) {
            return false;
        }
        return required;
    }

    public String getDefaultValue() {
        return resolveByEntityChain(DEFAULT_VALUE);
    }

    public String getValue() {
        String value = resolveByEntityChain(VALUE);
        if (Objects.isNull(value)) {
            return getDefaultValue();
        }
        return value;
    }
}
