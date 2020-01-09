package io.github.edmm.model.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.core.parser.support.DefaultKeys;
import io.github.edmm.model.Metadata;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.apache.commons.lang3.reflect.ConstructorUtils;

public abstract class TypeWrapper {

    public static Map<String, RootComponent> wrapComponents(EntityGraph graph) {
        Map<String, RootComponent> components = new HashMap<>();
        Iterator<Entity> it = graph.getEntity(EntityGraph.COMPONENTS)
                .orElseThrow(IllegalStateException::new).getChildren().iterator();
        while (it.hasNext()) {
            MappingEntity entity = (MappingEntity) it.next();
            String type = entity.getValue(RootComponent.TYPE);
            Class clazz = TypeResolver.resolve(type);
            components.put(entity.getName(), doWrap(entity, clazz));
        }
        return components;
    }

    @SuppressWarnings("unchecked")
    public static <T> T wrapScalarEntity(ScalarEntity entity, Class<T> targetType) {
        String value = entity.getValue();
        if (String.class.isAssignableFrom(targetType)) {
            return (T) value;
        } else if (Integer.class.isAssignableFrom(targetType)) {
            return (T) Integer.valueOf(value);
        } else if (Boolean.class.isAssignableFrom(targetType)) {
            return (T) Boolean.valueOf(value);
        } else {
            throw new IllegalStateException(String.format("Cannot get value of type '%s' from entity '%s'", targetType, entity));
        }
    }

    public static <T> T wrapModelEntity(MappingEntity entity, Class<T> type) {
        if (entity == null) {
            return null;
        }
        return doWrap(entity, type);
    }

    public static Metadata wrapMetadata(MappingEntity mappingEntity) {
        Metadata values = new Metadata();
        for (Entity child : mappingEntity.getChildren()) {
            if (child instanceof ScalarEntity) {
                ScalarEntity scalarEntity = (ScalarEntity) child;
                values.put(scalarEntity.getName(), scalarEntity.getValue());
            }
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    private static <T> T doWrap(MappingEntity entity, Class type) {
        try {
            return (T) ConstructorUtils.invokeConstructor(type, entity);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Failed to wrap entity '%s' in type '%s'", entity, type), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static RootRelation wrapRelation(MappingEntity relationEntity, MappingEntity componentEntity) {
        ScalarEntity type = (ScalarEntity) relationEntity.getChild(DefaultKeys.TYPE).orElseThrow(IllegalArgumentException::new);
        Class clazz = TypeResolver.resolve(type.getValue());
        try {
            return (RootRelation) ConstructorUtils.invokeConstructor(clazz, relationEntity, componentEntity);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Failed to wrap relation '%s'", relationEntity), e);
        }
    }
}
