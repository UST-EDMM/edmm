package io.github.edmm.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CastUtil {

    public static Map<String, Object> safelyCastToStringObjectMap(Object objectToCast) {
        return castToStringObjectMapIfPresent(checkMapForCast(objectToCast));
    }

    public static Map<String, String> safelyCastToStringStringMap(Object objectToCast) {
        return castToStringStringMapIfPresent(checkMapForCast(objectToCast));
    }

    public static List<Object> safelyCastToObjectList(Object objectToCast) {
        return castToObjectListIfPresent(checkListForCast(objectToCast));
    }

    public static List<String> safelyCastToStringList(Object objectToCast) {
        return castToStringListIfPresent(checkListForCast(objectToCast));
    }

    private static List<Object> castToObjectListIfPresent(Optional<List> listOptional) {
        return listOptional.isPresent() ? castToListOf(Object.class, listOptional.get()) : Collections.emptyList();
    }

    private static List<String> castToStringListIfPresent(Optional<List> listOptional) {
        return listOptional.isPresent() ? castToListOf(String.class, listOptional.get()) : Collections.emptyList();
    }

    private static Map<String, Object> castToStringObjectMapIfPresent(Optional<Map> mapOptional) {
        return mapOptional.isPresent() ? castToMapOf(String.class, Object.class, mapOptional.get()) : Collections.emptyMap();
    }

    private static Optional<Map> checkMapForCast(Object objectToCast) {
        return Optional.ofNullable(objectToCast)
            .filter(Map.class::isInstance)
            .map(Map.class::cast);
    }

    private static Map<String, String> castToStringStringMapIfPresent(Optional<Map> mapOptional) {
        return mapOptional.isPresent() ? castToMapOf(String.class, String.class, mapOptional.get()) : Collections.emptyMap();
    }

    private static Optional<List> checkListForCast(Object objectToCast) {
        return Optional.ofNullable(objectToCast)
            .filter(List.class::isInstance)
            .map(List.class::cast);
    }

    private static <K> List<K> castToListOf(Class<K> kClass, List<?> listToCast) {
        listToCast.forEach(entry -> checkCast(kClass, entry));

        @SuppressWarnings("unchecked")
        List<K> result = (List<K>) listToCast;
        return result;
    }

    private static <K, V> Map<K, V> castToMapOf(Class<K> kClass, Class<V> vClass, Map<?, ?> map) {

        for (Map.Entry<?, ?> e : map.entrySet()) {
            checkCast(kClass, e.getKey());
            checkCast(vClass, e.getValue());
        }

        @SuppressWarnings("unchecked")
        Map<K, V> result = (Map<K, V>) map;
        return result;
    }

    private static <T> void checkCast(Class<T> tClass, Object objectToCast) {
        if (!tClass.isInstance(objectToCast)) {
            throw new ClassCastException("Expected: " + tClass.getName() + "but was: " + objectToCast.getClass().getName()
            );
        }
    }
}
