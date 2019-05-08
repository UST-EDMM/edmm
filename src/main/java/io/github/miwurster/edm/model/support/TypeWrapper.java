package io.github.miwurster.edm.model.support;

import io.github.miwurster.edm.core.parser.MappingEntity;
import org.apache.commons.lang3.reflect.ConstructorUtils;

public abstract class TypeWrapper {

    public static <T> T wrap(MappingEntity entity, Class<T> type) {
        if (entity == null) {
            return null;
        }
        return doWrap(entity, type);
    }

    @SuppressWarnings("unchecked")
    private static <T> T doWrap(MappingEntity entity, Class type) {
        try {
            return (T) ConstructorUtils.invokeConstructor(type, entity);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Failed to wrap up entity '%s' in type '%s'", entity, type), e);
        }
    }
}
