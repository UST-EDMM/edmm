package io.github.edmm.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class CastUtil {

    @SuppressWarnings("rawtypes")
    public static Map<String, Object> safelyCastToStringObjectMap(Object objectToCast) {
        Optional<Map> mapOptional = checkMapForCast(objectToCast);
        return mapOptional.map(CastUtil::castToStringObjectMap).orElse(Collections.emptyMap());
    }

    @SuppressWarnings("rawtypes")
    public static Optional<Map<String, Object>> safelyCastToStringObjectMapOptional(Object objectToCast) {
        Optional<Map> mapOptional = checkMapForCast(objectToCast);
        return mapOptional.map(CastUtil::castToStringObjectMap);
    }

    @SuppressWarnings("rawtypes")
    public static Map<String, String> safelyCastToStringStringMap(Object objectToCast) {
        Optional<Map> mapOptional = checkMapForCast(objectToCast);
        return mapOptional.map(CastUtil::castToStringStringMap).orElse(Collections.emptyMap());
    }

    @SuppressWarnings("rawtypes")
    public static List<Object> safelyCastToObjectList(Object objectToCast) {
        Optional<List> listOptional = checkListForCast(objectToCast);
        return listOptional.map(CastUtil::castToObjectList).orElse(Collections.emptyList());
    }

    @SuppressWarnings("rawtypes")
    public static List<String> safelyCastToStringList(Object objectToCast) {
        Optional<List> listOptional = checkListForCast(objectToCast);
        return listOptional.map(CastUtil::castToStringList).orElse(Collections.emptyList());
    }

    @SuppressWarnings("rawtypes")
    private static Map<String, Object> castToStringObjectMap(Map map) {
        return castToMapOf(String.class, Object.class, map);
    }

    @SuppressWarnings("rawtypes")
    private static Map<String, String> castToStringStringMap(Map map) {
        return castToMapOf(String.class, String.class, map);
    }

    @SuppressWarnings("rawtypes")
    private static List<Object> castToObjectList(List list) {
        return castToListOf(Object.class, list);
    }

    @SuppressWarnings("rawtypes")
    private static List<String> castToStringList(List list) {
        return castToListOf(String.class, list);
    }

    @SuppressWarnings("rawtypes")
    private static Optional<Map> checkMapForCast(Object objectToCast) {
        return Optional.ofNullable(objectToCast).filter(Map.class::isInstance).map(Map.class::cast);
    }

    @SuppressWarnings("rawtypes")
    private static Optional<List> checkListForCast(Object objectToCast) {
        return Optional.ofNullable(objectToCast).filter(List.class::isInstance).map(List.class::cast);
    }

    @SuppressWarnings("SameParameterValue")
    private static <K, V> Map<K, V> castToMapOf(Class<K> kClass, Class<V> vClass, Map<?, ?> map) {

        for (Map.Entry<?, ?> e : map.entrySet()) {
            checkCast(kClass, e.getKey());
            checkCast(vClass, e.getValue());
        }

        @SuppressWarnings("unchecked") Map<K, V> result = (Map<K, V>) map;
        return result;
    }

    private static <K> List<K> castToListOf(Class<K> kClass, List<?> listToCast) {
        listToCast.forEach(entry -> checkCast(kClass, entry));

        @SuppressWarnings("unchecked") List<K> result = (List<K>) listToCast;
        return result;
    }

    private static <T> void checkCast(Class<T> tClass, Object objectToCast) {
        if (!tClass.isInstance(objectToCast)) {
            throw new ClassCastException("Expected: " + tClass.getName() + "but was: " + objectToCast.getClass()
                .getName());
        }
    }
}
