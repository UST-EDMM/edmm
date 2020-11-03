package io.github.edmm.core.parser;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScalarEntity)) return false;
        if (!super.equals(o)) return false;
        ScalarEntity that = (ScalarEntity) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
