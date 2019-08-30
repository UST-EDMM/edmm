package io.github.edmm.core.transformation;

import java.util.Objects;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public final class Platform {

    public static final Platform NOOP = Platform.builder().id("noop").name("noop").build();

    private final String id;
    private final String name;

    public Platform(@NonNull String id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Platform platform = (Platform) object;
        return Objects.equals(id, platform.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
