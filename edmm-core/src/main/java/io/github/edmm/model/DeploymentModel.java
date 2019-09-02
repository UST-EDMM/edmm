package io.github.edmm.model;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.support.TypeWrapper;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@ToString
public final class DeploymentModel {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentModel.class);

    private final String name;
    private final EntityGraph graph;

    private final Map<String, RootComponent> componentMap;
    private final Graph<RootComponent, RootRelation> topology = new DirectedMultigraph<>(RootRelation.class);
    private Set<Graph<RootComponent, RootRelation>> stacks = new HashSet<>();

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

    public EdgeReversedGraph<RootComponent, RootRelation> getReversedTopology() {
        return new EdgeReversedGraph<>(topology);
    }

    public Set<Graph<RootComponent, RootRelation>> findComponentStacks() {
        EdgeReversedGraph<RootComponent, RootRelation> dependencyGraph = new EdgeReversedGraph<>(topology);
        List<RootComponent> stackSources = topology.vertexSet()
                .stream()
                .filter(v -> dependencyGraph.inDegreeOf(v) == 0)
                .collect(Collectors.toList());

        Set<Graph<RootComponent, RootRelation>> stacks = new HashSet<>();
        stackSources.forEach(source -> {
            Graph<RootComponent, RootRelation> stack = new DirectedMultigraph<>(RootRelation.class);
            constructGraph(dependencyGraph, source, stack);
            stacks.add(stack);
        });

        return stacks;
    }

    private void constructGraph(EdgeReversedGraph<RootComponent, RootRelation> dependencyGraph, RootComponent currentNode, Graph<RootComponent, RootRelation> stack) {
        if (dependencyGraph.outDegreeOf(currentNode) != 0) {
            dependencyGraph.outgoingEdgesOf(currentNode).forEach(edge -> {
                if (edge instanceof HostedOn) {
                    stack.addVertex(currentNode);
                    RootComponent newVertex = dependencyGraph.getEdgeTarget(edge);
                    stack.addVertex(newVertex);
                    stack.addEdge(currentNode, newVertex, edge);
                    constructGraph(dependencyGraph, newVertex, stack);
                }
            });
        } else {
            stack.addVertex(currentNode);
        }
    }
}
