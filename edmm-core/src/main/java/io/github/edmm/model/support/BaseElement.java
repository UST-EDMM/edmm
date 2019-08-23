package io.github.edmm.model.support;

import java.util.Collection;

import io.github.edmm.core.parser.MappingEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BaseElement {

    protected final MappingEntity entity;

    public BaseElement(MappingEntity entity) {
        this.entity = entity;
    }

    public String getName() {
        return entity.getName();
    }

    public String getNormalizedName() {
        return entity.getName()
                .toLowerCase()
                .replace(".", "_");
    }

    protected <T> void set(Attribute<T> key, T value) {
        entity.setValue(key, value);
    }

    protected <T> void setDefault(Attribute<T> key, T value) {
        if (entity.getValue(key) == null) {
            set(key, value);
        }
    }

    protected <T> T get(Attribute<T> key) {
        return entity.getValue(key);
    }

    protected <T> Collection<T> getCollection(Attribute<T> key) {
        return entity.getCollection(key);
    }
}
