package io.github.ust.edmm.model.support;

import java.util.Collection;

import io.github.ust.edmm.core.parser.MappingEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public abstract class BaseElement {

    protected final MappingEntity entity;

    public BaseElement(MappingEntity entity) {
        this.entity = entity;
    }

    public String getName() {
        return entity.getName();
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
