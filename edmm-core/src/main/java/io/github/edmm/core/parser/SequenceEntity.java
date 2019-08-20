package io.github.edmm.core.parser;

public class SequenceEntity extends Entity {

    public SequenceEntity(EntityId id, EntityGraph graph) {
        super(id, graph);
    }

    @Override
    public String toString() {
        return String.format("SequenceEntity (id='%s')", getId());
    }
}
