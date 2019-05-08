package io.github.miwurster.edm.core.parser;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;

@Getter
public abstract class Entity implements Comparable<Entity> {

    protected final EntityId id;
    protected final EntityGraph graph;

    public Entity(EntityId id, EntityGraph graph) {
        this.id = id;
        this.graph = graph;
    }

    public EntityId getId() {
        return id;
    }

    public String getName() {
        return id.getName();
    }

    public Optional<Entity> getParent() {
        EntityId parentId = getId().getParent();
        if (parentId == null) {
            return Optional.empty();
        }
        return graph.getEntity(parentId);
    }

    public Set<Entity> getChildren() {
        Set<Entity> children = new HashSet<>();
        for (EntityGraph.Edge e : graph.outgoingEdgesOf(this)) {
            children.add(e.getTarget());
        }
        return children;
    }

    public Optional<Entity> getChild(String name) {
        for (EntityGraph.Edge edge : graph.outgoingEdgesOf(this)) {
            if (edge.getName().equals(name)) {
                return Optional.of(edge.getTarget());
            }
        }
        return Optional.empty();
    }

    @Override
    public int compareTo(Entity o) {
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
