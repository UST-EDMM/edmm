package io.github.miwurster.edm.model;

import lombok.Getter;

@Getter
public class Attribute<T> {

    private final String name;
    private final Class<T> type;

    public Attribute(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }
}
