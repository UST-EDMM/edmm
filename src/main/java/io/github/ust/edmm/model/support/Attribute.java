package io.github.ust.edmm.model.support;

import java.util.Optional;

import lombok.Getter;

@Getter
public class Attribute<T> {

    private final String name;
    private final Class<T> type;

    private Attribute<?> predecessor = null;

    public Attribute(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public Attribute(Attribute<?> predecessor, String name, Class<T> type) {
        this(name, type);
        this.predecessor = predecessor;
    }

    public Optional<Attribute<?>> getPredecessor() {
        return Optional.ofNullable(predecessor);
    }
}
