package io.github.edmm.model;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.support.DescribableElement;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static io.github.edmm.model.support.ModelEntity.PROPERTIES;

@ToString
@EqualsAndHashCode(callSuper = true)
public class Property extends DescribableElement {

    public static Attribute<String> TYPE = new Attribute<>("type", String.class);
    public static Attribute<Boolean> REQUIRED = new Attribute<>("required", Boolean.class);
    public static Attribute<String> DEFAULT_VALUE = new Attribute<>("default_value", String.class);
    public static Attribute<String> VALUE = new Attribute<>("value", String.class);

    private final MappingEntity entity;

    public Property(MappingEntity propertyDefinition, MappingEntity entity) {
        super(propertyDefinition);
        this.entity = entity;
    }

    public String getType() {
        String type = get(TYPE);
        if (Objects.isNull(type)) {
            throw new IllegalStateException("Property needs to have a type");
        }
        return type;
    }

    public Boolean isRequired() {
        Boolean required = get(REQUIRED);
        if (Objects.isNull(required)) {
            return false;
        }
        return required;
    }

    public String getDefault() {
        return get(DEFAULT_VALUE);
    }

    public String getValue() {
        Optional<Entity> propertiesEntity = entity.getChild(PROPERTIES);
        if (propertiesEntity.isPresent()) {
            Set<Entity> children = propertiesEntity.get().getChildren();
            for (Entity e : children) {
                MappingEntity prop = (MappingEntity) e;
                if (e.getName().equals(getEntity().getName())) {
                    Optional<Entity> child = prop.getChild(VALUE);
                    if (child.isPresent()) {
                        return ((ScalarEntity) child.get()).getValue();
                    }
                }
            }
        }
        return getDefault();
    }
}
