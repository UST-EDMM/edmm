package io.github.miwurster.edm.core.parser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
