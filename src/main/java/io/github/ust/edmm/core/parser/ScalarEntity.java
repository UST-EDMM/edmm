package io.github.ust.edmm.core.parser;

import lombok.Getter;

@Getter
public class ScalarEntity extends Entity {

    private String value;

    public ScalarEntity(String value, EntityId id, EntityGraph graph) {
        super(id, graph);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("ScalarEntity (id='%s', value='%s')", getId(), getValue());
    }
}
