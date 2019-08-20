package io.github.edmm.core.parser.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.core.parser.MappingEntity;

public abstract class GraphHelper {

    public static Optional<MappingEntity> findTypeEntity(EntityGraph graph, MappingEntity entity) {
        MappingEntity type = null;
        for (EntityGraph.Edge edge : graph.outgoingEdgesOf(entity)) {
            if (edge.getName().equals("instance_of")) {
                type = (MappingEntity) edge.getTarget();
            }
        }
        return Optional.ofNullable(type);
    }

    public static Optional<MappingEntity> findParentEntity(EntityGraph graph, MappingEntity entity) {
        MappingEntity parent = null;
        for (EntityGraph.Edge edge : graph.outgoingEdgesOf(entity)) {
            if (edge.getName().equals("extends_type")) {
                parent = (MappingEntity) edge.getTarget();
            }
        }
        return Optional.ofNullable(parent);
    }

    public static List<MappingEntity> resolveInheritanceChain(EntityGraph graph, MappingEntity entity) {
        List<MappingEntity> entities = new ArrayList<>();
        entities.add(entity);
        Optional<MappingEntity> optionalParent = findParentEntity(graph, entity);
        while (optionalParent.isPresent()) {
            MappingEntity parent = optionalParent.get();
            entities.add(parent);
            optionalParent = findParentEntity(graph, parent);
        }

        Collections.reverse(entities);
        return entities;
    }

    /**
     * Used to find the MappingEntity that is referenced by another one. For example, if a relation uses a certain type
     * this method will resolve the corresponding type entity.
     *
     * @param graph      The graph object to operate on
     * @param entityName The name of the entity to find
     * @param entryPoint The EntityId as entry point, e.g., "components", "relation_types".
     * @return The found entity object
     */
    public static Optional<Entity> findMappingEntity(EntityGraph graph, String entityName, EntityId entryPoint) {
        for (Entity node : graph.getChildren(entryPoint)) {
            if (node instanceof MappingEntity
                    && entityName.equals(node.getName())) {
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }
}
