package io.github.edmm.core.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.github.edmm.utils.Consts;
import lombok.Getter;

@Getter
public class EntityId implements Comparable<EntityId> {

    private final String name;

    private final List<String> path;

    public EntityId() {
        this(new ArrayList<>());
    }

    public EntityId(String... path) {
        this(Arrays.asList(path));
    }

    public EntityId(List<String> path) {
        this.path = Collections.unmodifiableList(new ArrayList<>(path));
        if (path.isEmpty()) {
            this.name = Consts.EMPTY;
        } else {
            this.name = path.get(path.size() - 1);
        }
    }

    public static EntityId of(String... path) {
        return new EntityId(path);
    }

    public EntityId extend(String segment) {
        ArrayList<String> newPath = new ArrayList<>(this.path);
        newPath.add(segment);
        return new EntityId(newPath);
    }

    public EntityId getParent() {
        int size = this.path.size();
        if (size < 2) {
            return null;
        }
        return new EntityId(this.path.subList(0, size - 1));
    }

    @Override
    public int compareTo(EntityId other) {
        if (path.size() > other.path.size()) {
            return 1;
        } else if (path.size() < other.path.size()) {
            return -1;
        } else {
            for (int i = 0; i < path.size(); i++) {
                String segment = path.get(i);
                String otherSegment = other.path.get(i);
                int result = segment.compareTo(otherSegment);
                if (result != 0) {
                    return result;
                }
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            builder.append(path.get(i));
            if (i != path.size() - 1) {
                builder.append(".");
            }
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityId entityId = (EntityId) o;
        return Objects.equals(path, entityId.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
