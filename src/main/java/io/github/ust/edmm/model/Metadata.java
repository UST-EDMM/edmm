package io.github.ust.edmm.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Metadata implements Map<String, String> {

    private final Map<String, String> values = new LinkedHashMap<>();

    public static Metadata of(Map<String, String> values) {
        Metadata metadata = new Metadata();
        metadata.putAll(values);
        return metadata;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return values.get(key);
    }

    @Override
    public String put(String key, String value) {
        return values.put(key, value);
    }

    @Override
    public String remove(Object key) {
        return values.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        values.putAll(m);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public Set<String> keySet() {
        return values.keySet();
    }

    @Override
    public Collection<String> values() {
        return values.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return values.entrySet();
    }

    @Override
    public String getOrDefault(Object key, String defaultValue) {
        return values.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super String> action) {
        values.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super String, ? extends String> function) {
        values.replaceAll(function);
    }

    @Override
    public String putIfAbsent(String key, String value) {
        return values.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return values.remove(key, value);
    }

    @Override
    public boolean replace(String key, String oldValue, String newValue) {
        return values.replace(key, oldValue, newValue);
    }

    @Override
    public String replace(String key, String value) {
        return values.replace(key, value);
    }

    @Override
    public String computeIfAbsent(String key, Function<? super String, ? extends String> mappingFunction) {
        return values.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public String computeIfPresent(String key, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return values.computeIfPresent(key, remappingFunction);
    }

    @Override
    public String compute(String key, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return values.compute(key, remappingFunction);
    }

    @Override
    public String merge(String key, String value, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return values.merge(key, value, remappingFunction);
    }
}
