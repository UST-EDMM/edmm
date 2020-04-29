package io.github.edmm.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Util {

    private static Optional<Map> checkMapForCast(Object objectToCast) {
        return Optional.ofNullable(objectToCast)
            .filter(Map.class::isInstance)
            .map(Map.class::cast);
    }

    public static Map<String, Object> safelyCastToStringObjectMap(Object objectToCast) {
        Optional<Map> mapOptional = checkMapForCast(objectToCast);

        if (mapOptional.isPresent()) {
            return castToMapOf(String.class, Object.class, mapOptional.get());
        }
        return Collections.emptyMap();
    }

    public static Map<String, String> safelyCastToStringStringMap(Object objectToCast) {
        Optional<Map> mapOptional = checkMapForCast(objectToCast);

        if (mapOptional.isPresent()) {
            return castToMapOf(String.class, String.class, mapOptional.get());
        }
        return Collections.emptyMap();
    }

    private static <K, V> Map<K, V> castToMapOf(Class<K> clazzK, Class<V> clazzV, Map<?, ?> map) {

        for (Map.Entry<?, ?> e : map.entrySet()) {
            checkCast(clazzK, e.getKey());
            checkCast(clazzV, e.getValue());
        }

        @SuppressWarnings("unchecked")
        Map<K, V> result = (Map<K, V>) map;
        return result;
    }

    private static final String LS = System.getProperty("line.separator");

    /**
     * Check if cast would work
     */
    private static <T> void checkCast(Class<T> clazz, Object obj) {
        if (!clazz.isInstance(obj)) {
            throw new ClassCastException(
                LS + "Expected: " + clazz.getName() +
                    LS + "Was:      " + obj.getClass().getName() +
                    LS + "Value:    " + obj
            );
        }
    }

    public static List<Object> safelyCastToObjectList(Object objectToCast) {
        Optional<List> listOptional = checkListForCast(objectToCast);

        if (listOptional.isPresent()) {
            return castToListOf(Object.class, listOptional.get());
        }
        return Collections.emptyList();
    }

    public static List<String> safelyCastToStringList(Object objectToCast) {
        Optional<List> listOptional = checkListForCast(objectToCast);

        if (listOptional.isPresent()) {
            return castToListOf(String.class, listOptional.get());
        }
        return Collections.emptyList();
    }

    private static Optional<List> checkListForCast(Object objectToCast) {
        return Optional.ofNullable(objectToCast)
            .filter(List.class::isInstance)
            .map(List.class::cast);
    }

    private static <K> List<K> castToListOf(Class<K> classK, List<?> list) {
        list.forEach(entry -> checkCast(classK, entry));

        @SuppressWarnings("unchecked")
        List<K> result = (List<K>) list;
        return result;
    }
}
