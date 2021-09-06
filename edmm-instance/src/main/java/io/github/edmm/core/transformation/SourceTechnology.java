package io.github.edmm.core.transformation;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;

@Builder
public final class SourceTechnology {

    private final String id;
    private final String name;

    @JsonCreator
    public SourceTechnology(@NonNull @JsonProperty("id") String id, @NonNull @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SourceTechnology sourceTechnology = (SourceTechnology) object;
        return Objects.equals(id, sourceTechnology.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
