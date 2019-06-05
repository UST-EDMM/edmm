package io.github.ust.edmm.core.parser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import io.github.ust.edmm.model.Metadata;
import io.github.ust.edmm.model.support.Attribute;
import io.github.ust.edmm.model.support.ModelEntity;
import io.github.ust.edmm.model.support.TypeWrapper;

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
        Optional<Entity> entity = getChild(key);
        Class<T> targetType = key.getType();
        if (entity.isPresent()) {
            if (entity.get() instanceof ScalarEntity) {
                ScalarEntity scalarEntity = (ScalarEntity) entity.get();
                return TypeWrapper.wrapScalarEntity(scalarEntity, targetType);
            } else if (ModelEntity.class.isAssignableFrom(targetType)) {
                MappingEntity mappingEntity = (MappingEntity) entity.get();
                return TypeWrapper.wrapModelEntity(mappingEntity, targetType);
            } else if (Metadata.class.isAssignableFrom(targetType)) {
                MappingEntity mappingEntity = (MappingEntity) entity.get();
                return (T) TypeWrapper.wrapMetadata(mappingEntity);
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
                    values.add(TypeWrapper.wrapModelEntity(mappingEntity, targetType));
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
