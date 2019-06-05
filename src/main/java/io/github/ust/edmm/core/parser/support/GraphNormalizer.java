package io.github.ust.edmm.core.parser.support;

import java.util.Optional;

import io.github.ust.edmm.core.parser.Entity;
import io.github.ust.edmm.core.parser.EntityGraph;
import io.github.ust.edmm.core.parser.EntityId;
import io.github.ust.edmm.core.parser.MappingEntity;
import io.github.ust.edmm.core.parser.ScalarEntity;
import io.github.ust.edmm.core.parser.SequenceEntity;

import static io.github.ust.edmm.core.parser.EntityGraph.COMPONENTS;
import static io.github.ust.edmm.core.parser.EntityGraph.COMPONENT_TYPES;
import static io.github.ust.edmm.core.parser.EntityGraph.RELATION_TYPES;

public abstract class GraphNormalizer {

    public static void normalize(EntityGraph graph) {
        resolveExtends(graph, COMPONENT_TYPES);
        resolveExtends(graph, RELATION_TYPES);
        resolveComponentTypes(graph);
        resolveRelationTypes(graph);
        normalizeRelations(graph);
        resolveRelations(graph);
        normalizeOperations(graph);
        normalizeProperties(graph);
    }

    private static void resolveExtends(EntityGraph graph, EntityId types) {
        for (Entity node : graph.getChildren(types)) {
            Optional<Entity> entity = node.getChild("extends");
            if (entity.isPresent()) {
                ScalarEntity extendsAssignment = (ScalarEntity) entity.get();
                if (extendsAssignment.getValue() != null) {
                    GraphHelper
                            .findMappingEntity(graph, extendsAssignment.getValue(), types)
                            .ifPresent(value -> graph.addEdge(node, value, "extends_type"));
                }
            }
        }
    }

    private static void resolveComponentTypes(EntityGraph graph) {
        for (Entity node : graph.getChildren(COMPONENTS)) {
            Optional<Entity> entity = node.getChild("type");
            if (entity.isPresent()) {
                ScalarEntity typeAssignment = (ScalarEntity) entity.get();
                GraphHelper
                        .findMappingEntity(graph, typeAssignment.getValue(), COMPONENT_TYPES)
                        .ifPresent(value -> graph.addEdge(node, value, "instance_of"));
            }
        }
    }

    private static void resolveRelationTypes(EntityGraph graph) {
        for (Entity node : graph.getChildren(COMPONENTS)) {
            Optional<Entity> relations = node.getChild("relations");
            if (relations.isPresent()) {
                for (Entity relation : relations.get().getChildren()) {
                    GraphHelper
                            .findMappingEntity(graph, relation.getName(), RELATION_TYPES)
                            .ifPresent(value -> graph.addEdge(relation, value, "instance_of"));
                }
            }
        }
    }

    private static void resolveRelations(EntityGraph graph) {
        for (Entity node : graph.getChildren(COMPONENTS)) {
            Optional<Entity> relations = node.getChild("relations");
            if (relations.isPresent()) {
                for (Entity relation : relations.get().getChildren()) {
                    Optional<Entity> entity = relation.getChild("target");
                    if (entity.isPresent()) {
                        ScalarEntity targetAssignment = (ScalarEntity) entity.get();
                        GraphHelper
                                .findMappingEntity(graph, targetAssignment.getValue(), COMPONENTS)
                                .ifPresent(value -> graph.addEdge(relation, value, "target_component"));
                    }
                }
            }
        }
    }

    private static void normalizeRelations(EntityGraph graph) {
        for (Entity node : graph.getChildren(COMPONENTS)) {
            Optional<Entity> relations = node.getChild("relations");
            if (relations.isPresent()) {
                for (Entity relation : relations.get().getChildren()) {
                    if (relation instanceof ScalarEntity) {
                        ScalarEntity scalarEntity = (ScalarEntity) relation;
                        MappingEntity normalizedEntity = new MappingEntity(scalarEntity.getId(), graph);
                        ScalarEntity target = new ScalarEntity(scalarEntity.getValue(), normalizedEntity.getId().extend("target"), graph);
                        graph.replaceEntity(scalarEntity, normalizedEntity);
                        graph.addEntity(target);
                    }
                }
            }
        }
    }

    private static void normalizeOperations(EntityGraph graph) {
        for (Entity node : graph.getChildren(COMPONENTS)) {
            doNormalizeOperations(graph, node);
        }
        for (Entity node : graph.getChildren(COMPONENT_TYPES)) {
            doNormalizeOperations(graph, node);
        }
        for (Entity node : graph.getChildren(RELATION_TYPES)) {
            doNormalizeOperations(graph, node);
        }
    }

    private static void doNormalizeOperations(EntityGraph graph, Entity node) {
        Optional<Entity> operations = node.getChild("operations");
        if (operations.isPresent()) {
            for (Entity op : operations.get().getChildren()) {
                if (op instanceof ScalarEntity) {
                    ScalarEntity scalarEntity = (ScalarEntity) op;
                    MappingEntity normalizedEntity = new MappingEntity(scalarEntity.getId(), graph);
                    SequenceEntity artifacts = new SequenceEntity(op.getId().extend("artifacts"), graph);
                    ScalarEntity artifact = new ScalarEntity(scalarEntity.getValue(), artifacts.getId().extend("cmd"), graph);
                    graph.replaceEntity(scalarEntity, normalizedEntity);
                    graph.addEntity(artifacts);
                    graph.addEntity(artifact);
                }
            }
        }
    }

    private static void normalizeProperties(EntityGraph graph) {
        for (Entity node : graph.getChildren(COMPONENTS)) {
            doNormalizeProperties(graph, node);
        }
    }

    private static void doNormalizeProperties(EntityGraph graph, Entity node) {
        Optional<Entity> properties = node.getChild("properties");
        if (properties.isPresent()) {
            for (Entity prop : properties.get().getChildren()) {
                if (prop instanceof ScalarEntity) {
                    ScalarEntity scalarEntity = (ScalarEntity) prop;
                    MappingEntity normalizedEntity = new MappingEntity(scalarEntity.getId(), graph);
                    ScalarEntity type = new ScalarEntity("string", normalizedEntity.getId().extend("type"), graph);
                    ScalarEntity value = new ScalarEntity(scalarEntity.getValue(), normalizedEntity.getId().extend("value"), graph);
                    graph.replaceEntity(scalarEntity, normalizedEntity);
                    graph.addEntity(type);
                    graph.addEntity(value);
                }
            }
        }
    }
}
