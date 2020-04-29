package io.github.edmm.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CastUtil {

    public static Map<String, Object> safelyCastToStringObjectMap(Object objectToCast) {
        Optional<Map> mapOptional = checkMapForCast(objectToCast);
        return mapOptional.isPresent() ? castToStringObjectMap(mapOptional.get()) : Collections.emptyMap();
    }

    public static Map<String, String> safelyCastToStringStringMap(Object objectToCast) {
        Optional<Map> mapOptional = checkMapForCast(objectToCast);
        return mapOptional.isPresent() ? castToStringStringMap(mapOptional.get()) : Collections.emptyMap();
    }

    public static List<Object> safelyCastToObjectList(Object objectToCast) {
        Optional<List> listOptional = checkListForCast(objectToCast);
        return listOptional.isPresent() ? castToObjectList(listOptional.get()) : Collections.emptyList();
    }

    public static List<String> safelyCastToStringList(Object objectToCast) {
        Optional<List> listOptional = checkListForCast(objectToCast);
        return listOptional.isPresent() ? castToStringList(listOptional.get()) : Collections.emptyList();
    }

    private static Map<String, Object> castToStringObjectMap(Map map) {
        return castToMapOf(String.class, Object.class, map);
    }

    private static Map<String, String> castToStringStringMap(Map map) {
        return castToMapOf(String.class, String.class, map);
    }

    private static List<Object> castToObjectList(List list) {
        return castToListOf(Object.class, list);
    }

    private static List<String> castToStringList(List list) {
        return castToListOf(String.class, list);
    }

    private static Optional<Map> checkMapForCast(Object objectToCast) {
        return Optional.ofNullable(objectToCast)
            .filter(Map.class::isInstance)
            .map(Map.class::cast);
    }

    private static Optional<List> checkListForCast(Object objectToCast) {
        return Optional.ofNullable(objectToCast)
            .filter(List.class::isInstance)
            .map(List.class::cast);
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

    private static <K> List<K> castToListOf(Class<K> kClass, List<?> listToCast) {
        listToCast.forEach(entry -> checkCast(kClass, entry));

        @SuppressWarnings("unchecked")
        List<K> result = (List<K>) listToCast;
        return result;
    }

    private static <T> void checkCast(Class<T> tClass, Object objectToCast) {
        if (!tClass.isInstance(objectToCast)) {
            throw new ClassCastException("Expected: " + tClass.getName() + "but was: " + objectToCast.getClass().getName()
            );
        }
    }
}
