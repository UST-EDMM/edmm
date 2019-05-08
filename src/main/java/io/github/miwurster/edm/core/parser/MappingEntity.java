package io.github.miwurster.edm.core.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.github.miwurster.edm.model.Attribute;
import io.github.miwurster.edm.model.ModelEntity;
import io.github.miwurster.edm.model.support.TypeWrapper;

public class MappingEntity extends Entity {

    public MappingEntity(EntityId id, EntityGraph graph) {
        super(id, graph);
    }

    public <T> void setValue(Attribute<T> key, T value) {
        final EntityId id = this.id.extend(key.getName());
        final Entity entity;
        if (ModelEntity.class.isAssignableFrom(key.getType())) {
            entity = ((ModelEntity) value).getEntity();
        } else {
            String normalizedValue = (value == null) ? null : value.toString();
            entity = new ScalarEntity(normalizedValue, id, graph);
        }
        Optional<Entity> e = graph.getEntity(id);
        if (e.isPresent()) {
            graph.replaceEntity(e.get(), entity);
        } else {
            graph.addEntity(entity);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(Attribute<T> key) {
        Optional<Entity> entity = getChild(key.getName());
        Class<T> targetType = key.getType();
        if (entity.isPresent()) {
            if (entity.get() instanceof ScalarEntity) {
                ScalarEntity scalarEntity = (ScalarEntity) entity.get();
                String value = scalarEntity.getValue();
                if (String.class.isAssignableFrom(targetType)) {
                    return (T) value;
                } else if (Integer.class.isAssignableFrom(targetType)) {
                    return (T) Integer.valueOf(value);
                } else if (Boolean.class.isAssignableFrom(targetType)) {
                    return (T) Boolean.valueOf(value);
                } else {
                    throw new IllegalStateException(String.format("Cannot get value of type '%s' from entity '%s'", targetType, entity));
                }
            } else if (ModelEntity.class.isAssignableFrom(targetType)) {
                MappingEntity mappingEntity = (MappingEntity) entity.get();
                return TypeWrapper.wrap(mappingEntity, targetType);
            } else if (Map.class.isAssignableFrom(targetType)) {
                Map<String, Object> values = new HashMap<>();
                MappingEntity mappingEntity = (MappingEntity) entity.get();
                for (Entity child : mappingEntity.getChildren()) {
                    if (child instanceof ScalarEntity) {
                        ScalarEntity scalarEntity = (ScalarEntity) child;
                        values.put(scalarEntity.getName(), scalarEntity.getValue());
                    }
                }
                return (T) values;
            } else {
                throw new IllegalStateException(String.format("Cannot get value of type '%s' from entity '%s'", targetType, entity));
            }
        }
        return null;
    }

    public <T> Collection<T> getCollection(Attribute<T> key) {
        Set<T> values = new HashSet<>();
        Optional<Entity> entity = getChild(key.getName());
        Class<T> targetType = key.getType();
        if (entity.isPresent()) {
            for (Entity child : entity.get().getChildren()) {
                if (ModelEntity.class.isAssignableFrom(targetType)) {
                    MappingEntity mappingEntity = (MappingEntity) child;
                    values.add(TypeWrapper.wrap(mappingEntity, targetType));
                } else {
                    throw new IllegalStateException(String.format("Cannot get value of type '%s' from entity '%s'", targetType, entity));
                }
            }
        }
        return values;
    }

    @Override
    public String toString() {
        return (String.format("MappingEntity (id='%s')", getId()));
    }
}
