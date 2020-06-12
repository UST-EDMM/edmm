package io.github.edmm.core.parser;

import lombok.Getter;

@Getter
public class ScalarEntity extends Entity {

    private final String value;

    public ScalarEntity(String value, EntityId id, EntityGraph graph) {
        super(id, graph);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("ScalarEntity (id='%s', value='%s')", getId(), getValue());
    }
}
