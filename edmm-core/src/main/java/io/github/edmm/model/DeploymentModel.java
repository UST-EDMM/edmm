package io.github.edmm.model;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.support.TypeWrapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMultigraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@ToString
@EqualsAndHashCode
public final class DeploymentModel {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentModel.class);

    private final String name;
    private final EntityGraph graph;

    private final Map<String, RootComponent> componentMap;
    private final Graph<RootComponent, RootRelation> topology = new DirectedMultigraph<>(RootRelation.class);

    public DeploymentModel(String name, EntityGraph graph) {
        this.name = name;
        this.graph = graph;
        componentMap = TypeWrapper.wrapComponents(graph);
        initNodes();
        initEdges();
    }

    private void initNodes() {
        componentMap.forEach((name, component) -> {
            topology.addVertex(component);
        });
    }

    private void initEdges() {
        for (RootComponent sourceComponent : topology.vertexSet()) {
            for (RootRelation relation : sourceComponent.getRelations()) {
                Optional<RootComponent> targetComponent = getComponent(relation.getTarget());
                targetComponent.ifPresent(value -> {
                    if (!topology.addEdge(sourceComponent, value, relation)) {
                        logger.error("Failed initializing topology");
                        throw new IllegalStateException("Failed initializing topology");
                    }
                });
            }
        }
    }

    @SneakyThrows
    public static DeploymentModel of(File file) {
        if (!file.isFile() || !file.canRead()) {
            throw new IllegalStateException(String.format("File '%s' does not exist - failed to construct internal graph", file));
        }
        EntityGraph graph = new EntityGraph(new FileInputStream(file));
        return new DeploymentModel(file.getName(), graph);
    }

    public Set<RootComponent> getComponents() {
        return topology.vertexSet();
    }

    public Optional<RootComponent> getComponent(String name) {
        return Optional.ofNullable(componentMap.get(name));
    }

    public Set<RootRelation> getRelations() {
        return topology.edgeSet();
    }
}
