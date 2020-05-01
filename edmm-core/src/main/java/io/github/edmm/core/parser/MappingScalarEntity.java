package io.github.edmm.core.parser;

import lombok.Getter;

public class MappingScalarEntity extends MappingEntity {
    @Getter
    private final String value;

    public MappingScalarEntity(EntityId id, EntityGraph graph, String value) {
        super(id, graph);
        this.value = value;
    }

    @Override
    public String toString() {
        return (String.format("MappingEntity (id='%s', value='%s')", getId(),getValue()));
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && this.value == ((MappingScalarEntity) o).value;
    }
}
