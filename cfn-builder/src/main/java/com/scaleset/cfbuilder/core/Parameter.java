package com.scaleset.cfbuilder.core;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Parameter implements Referenceable {

    @JsonIgnore
    private String id;

    private String type;

    private String description;

    @JsonProperty("Default")
    private Object defaultValue;

    private Boolean noEcho;

    private List<Object> allowedValues = new ArrayList<>();

    private String allowedPattern;

    private Integer maxLength;

    private Integer minLength;

    private Number maxValue;

    private Number minValue;

    private String constraintDescription;

    protected Parameter() {
    }

    public Parameter(String id, String type, String defaultValue, String description) {
        this.id = id;
        this.type = type;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public Parameter(String id, String type, Number defaultValue, String description) {
        this.id = id;
        this.type = type;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    public Parameter allowedPattern(String value) {
        this.allowedPattern = allowedPattern;
        return this;
    }

    public Parameter allowedValues(Object... values) {
        for (Object value : values) {
            this.allowedValues.add(value);
        }
        return this;
    }

    public Parameter constraintDescription(String value) {
        this.constraintDescription = value;
        return this;
    }

    public Parameter defaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public Parameter description(String description) {
        this.description = description;
        return this;
    }

    public String getAllowedPattern() {
        return allowedPattern;
    }

    public List<Object> getAllowedValues() {
        return allowedValues;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public Boolean getNoEcho() {
        return noEcho;
    }

    public String getType() {
        return type;
    }

    public Parameter id(String id) {
        this.id = id;
        return this;
    }

    public Parameter maxLength(Integer maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public Parameter maxValue(Number maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public Parameter minLength(Integer minLength) {
        this.minLength = minLength;
        return this;
    }

    public Parameter minValue(Number minValue) {
        this.minValue = minValue;
        return this;
    }

    public Parameter noEcho() {
        this.noEcho = true;
        return this;
    }

    public Parameter noEcho(boolean value) {
        this.noEcho = value;
        return this;
    }

    public Ref ref() {
        return new Ref(id);
    }

    public Parameter type(String type) {
        this.type = type;
        return this;
    }
}
