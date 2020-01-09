package io.github.edmm.core.parser.support;

import java.util.Optional;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.core.parser.SequenceEntity;

public abstract class GraphNormalizer {

    public static void normalize(EntityGraph graph) {
        resolveExtends(graph, EntityGraph.COMPONENT_TYPES);
        resolveExtends(graph, EntityGraph.RELATION_TYPES);
        resolveComponentTypes(graph);
        normalizeRelations(graph);
        resolveRelationTypes(graph);
        resolveRelations(graph);
        normalizeOperations(graph);
        normalizeProperties(graph);
    }

    private static void resolveExtends(EntityGraph graph, EntityId types) {
        for (Entity node : graph.getChildren(types)) {
            Optional<Entity> entity = node.getChild(DefaultKeys.EXTENDS);
            if (entity.isPresent()) {
                ScalarEntity extendsAssignment = (ScalarEntity) entity.get();
                if (extendsAssignment.getValue() != null) {
                    GraphHelper
                            .findMappingEntity(graph, extendsAssignment.getValue(), types)
                            .ifPresent(value -> graph.addEdge(node, value, DefaultKeys.EXTENDS_TYPE));
                }
            }
        }
    }

    private static void resolveComponentTypes(EntityGraph graph) {
        for (Entity node : graph.getChildren(EntityGraph.COMPONENTS)) {
            Optional<Entity> entity = node.getChild(DefaultKeys.TYPE);
            if (entity.isPresent()) {
                ScalarEntity typeAssignment = (ScalarEntity) entity.get();
                GraphHelper
                        .findMappingEntity(graph, typeAssignment.getValue(), EntityGraph.COMPONENT_TYPES)
                        .ifPresent(value -> graph.addEdge(node, value, DefaultKeys.INSTANCE_OF));
            }
        }
    }

    private static void resolveRelationTypes(EntityGraph graph) {
        for (Entity node : graph.getChildren(EntityGraph.COMPONENTS)) {
            Optional<Entity> relations = node.getChild(DefaultKeys.RELATIONS);
            if (relations.isPresent()) {
                for (Entity relation : relations.get().getChildren()) {
//                    if (relation instanceof ScalarEntity) {
//                        GraphHelper.findMappingEntity(graph, relation.getName(), EntityGraph.RELATION_TYPES)
//                                .ifPresent(value -> graph.addEdge(relation, value, DefaultKeys.INSTANCE_OF));
//                    } else {
                    relation.getChild(DefaultKeys.TYPE)
                            .flatMap(type -> GraphHelper.findMappingEntity(graph, ((ScalarEntity) type).getValue(), EntityGraph.RELATION_TYPES))
                            .ifPresent(value -> graph.addEdge(relation, value, DefaultKeys.INSTANCE_OF));
//                    }
                }
            }
        }
    }

    private static void resolveRelations(EntityGraph graph) {
        for (Entity node : graph.getChildren(EntityGraph.COMPONENTS)) {
            Optional<Entity> relations = node.getChild(DefaultKeys.RELATIONS);
            if (relations.isPresent()) {
                for (Entity relation : relations.get().getChildren()) {
                    Optional<Entity> entity = relation.getChild(DefaultKeys.TARGET);
                    if (entity.isPresent()) {
                        ScalarEntity targetAssignment = (ScalarEntity) entity.get();
                        GraphHelper
                                .findMappingEntity(graph, targetAssignment.getValue(), EntityGraph.COMPONENTS)
                                .ifPresent(value -> graph.addEdge(relation, value, DefaultKeys.TARGET_COMPONENT));
                    }
                }
            }
        }
    }

    private static void normalizeRelations(EntityGraph graph) {
        for (Entity node : graph.getChildren(EntityGraph.COMPONENTS)) {
            Optional<Entity> relations = node.getChild(DefaultKeys.RELATIONS);
            if (relations.isPresent()) {
                for (Entity relation : relations.get().getChildren()) {
                    if (relation instanceof ScalarEntity) {
                        ScalarEntity scalarEntity = (ScalarEntity) relation;
                        MappingEntity normalizedEntity = new MappingEntity(scalarEntity.getId(), graph);
                        ScalarEntity type = new ScalarEntity(scalarEntity.getName(), normalizedEntity.getId().extend(DefaultKeys.TYPE), graph);
                        ScalarEntity target = new ScalarEntity(scalarEntity.getValue(), normalizedEntity.getId().extend(DefaultKeys.TARGET), graph);
                        graph.replaceEntity(scalarEntity, normalizedEntity);
                        graph.addEntity(type);
                        graph.addEntity(target);
                    }
                }
            }
        }
    }

    private static void normalizeOperations(EntityGraph graph) {
        for (Entity node : graph.getChildren(EntityGraph.COMPONENTS)) {
            doNormalizeOperations(graph, node);
        }
        for (Entity node : graph.getChildren(EntityGraph.COMPONENT_TYPES)) {
            doNormalizeOperations(graph, node);
        }
        for (Entity node : graph.getChildren(EntityGraph.RELATION_TYPES)) {
            doNormalizeOperations(graph, node);
        }
    }

    private static void doNormalizeOperations(EntityGraph graph, Entity node) {
        Optional<Entity> operations = node.getChild(DefaultKeys.OPERATIONS);
        if (operations.isPresent()) {
            for (Entity op : operations.get().getChildren()) {
                if (op instanceof ScalarEntity) {
                    ScalarEntity scalarEntity = (ScalarEntity) op;
                    MappingEntity normalizedEntity = new MappingEntity(scalarEntity.getId(), graph);
                    SequenceEntity artifacts = new SequenceEntity(op.getId().extend(DefaultKeys.ARTIFACTS), graph);
                    ScalarEntity artifact = new ScalarEntity(scalarEntity.getValue(), artifacts.getId().extend(DefaultKeys.CMD), graph);
                    graph.replaceEntity(scalarEntity, normalizedEntity);
                    graph.addEntity(artifacts);
                    graph.addEntity(artifact);
                }
            }
        }
    }

    private static void normalizeProperties(EntityGraph graph) {
        for (Entity node : graph.getChildren(EntityGraph.COMPONENTS)) {
            doNormalizeProperties(graph, node);
        }
    }

    private static void doNormalizeProperties(EntityGraph graph, Entity node) {
        Optional<Entity> properties = node.getChild(DefaultKeys.PROPERTIES);
        if (properties.isPresent()) {
            for (Entity prop : properties.get().getChildren()) {
                if (prop instanceof ScalarEntity) {
                    ScalarEntity scalarEntity = (ScalarEntity) prop;
                    MappingEntity normalizedEntity = new MappingEntity(scalarEntity.getId(), graph);
                    ScalarEntity type = new ScalarEntity(DefaultKeys.STRING, normalizedEntity.getId().extend(DefaultKeys.TYPE), graph);
                    ScalarEntity value = new ScalarEntity(scalarEntity.getValue(), normalizedEntity.getId().extend(DefaultKeys.VALUE), graph);
                    graph.replaceEntity(scalarEntity, normalizedEntity);
                    graph.addEntity(type);
                    graph.addEntity(value);
                }
            }
        }
    }
}
