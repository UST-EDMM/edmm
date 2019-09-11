package com.scaleset.cfbuilder.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Fn {

    private Map<String, List<Object>> properties = new HashMap<>();

    @JsonIgnore
    private String name;

    @JsonIgnore
    private List<Object> parameters = new ArrayList<>();

    public Fn(String name, Object... params) {
        this.name = name;
        for (Object param : params) {
            parameters.add(param);
        }
        properties.put("Fn::" + name, parameters);
    }

    public static Fn fn(String name, Object... params) {
        return new Fn(name, params);
    }

    public static Fn fnDelimiter(String name, String delimiter, Object... params) {
        List<Object> list = Arrays.asList(params);
        return new Fn(name, delimiter, list);
    }

    public static Fn fnGetAtt(String resource, String attribute) {
        return new Fn("GetAtt", resource, attribute);
    }

    public String toString(Boolean yaml) {
        ObjectMapper mapper;
        if (yaml) {
            mapper = new ObjectMapper(new YAMLFactory());
        } else {
            mapper = new ObjectMapper();
        }
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        try {
            String result = mapper.writeValueAsString(this);
            return result.substring(4, result.length() - 1);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonAnyGetter
    protected Map<String, List<Object>> anyGetter() {
        return properties;
    }
}
