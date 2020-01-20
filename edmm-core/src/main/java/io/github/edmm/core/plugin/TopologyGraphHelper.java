package io.github.edmm.core.plugin;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Dbaas;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;
import org.jgrapht.Graph;

public abstract class TopologyGraphHelper {

    /**
     * Find the leafs in the graph (vertex that have no outgoing edges).
     *
     * @param graph The graph to search
     * @return mutable snapshot of all leaf vertices.
     */
    public static <V, E> Set<V> getLeafComponents(Graph<V, E> graph) {
        Set<V> vertexSet = graph.vertexSet();
        Set<V> leaves = new HashSet<>(vertexSet.size() * 2);
        for (V vertex : vertexSet) {
            if (graph.outgoingEdgesOf(vertex).isEmpty()) {
                leaves.add(vertex);
            }
        }
        return leaves;
    }

    /**
     * Fetch all of the dependents of the given target.
     *
     * @return mutable snapshot of the sources of all incoming edges
     */
    public static <V, E> Set<V> getSourceComponents(Graph<V, E> graph, V target, Class<? extends E> clazz) {
        Set<E> edges = graph.incomingEdgesOf(target);
        Set<V> sources = new LinkedHashSet<>();
        for (E edge : edges) {
            if (clazz.isInstance(edge)) {
                sources.add(graph.getEdgeSource(edge));
            }
        }
        return sources;
    }

    /**
     * Fetch all of the dependencies of the given source.
     *
     * @return mutable snapshot of the targets of all outgoing edges
     */
    public static <V, E> Set<V> getTargetComponents(Graph<V, E> graph, V source, Class<? extends E> clazz) {
        Set<E> edges = graph.outgoingEdgesOf(source);
        Set<V> targets = new LinkedHashSet<>();
        for (E edge : edges) {
            if (clazz.isInstance(edge)) {
                targets.add(graph.getEdgeTarget(edge));
            }
        }
        return targets;
    }

    @SuppressWarnings("unchecked")
    public static <V, E, T> List<T> getVertices(Graph<V, E> graph, Class<T> clazz) {
        return (List<T>) graph.vertexSet()
                .stream()
                .filter(clazz::isInstance)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <V, E, T> List<T> getEdges(Graph<V, E> graph, Class<T> clazz) {
        return (List<T>) graph.edgeSet()
                .stream()
                .filter(clazz::isInstance)
                .collect(Collectors.toList());
    }

    public static Optional<RootComponent> resolveHostingComponent(Graph<RootComponent, RootRelation> graph, RootComponent component) {
        Set<RootComponent> targetComponents = getTargetComponents(graph, component, HostedOn.class);
        Optional<RootComponent> optionalComponent = targetComponents.stream().findFirst();
        if (optionalComponent.isPresent()) {
            RootComponent hostingComponent = optionalComponent.get();
            if (hostingComponent instanceof Compute
                    || hostingComponent instanceof Dbaas
                    || hostingComponent instanceof Paas) {
                return Optional.of(hostingComponent);
            } else {
                return resolveHostingComponent(graph, hostingComponent);
            }
        } else {
            // Leaf reached
            return Optional.empty();
        }
    }

    public static Optional<Compute> resolveHostingComputeComponent(Graph<RootComponent, RootRelation> graph, RootComponent component) {
        Set<RootComponent> targetComponents = getTargetComponents(graph, component, HostedOn.class);
        Optional<RootComponent> optionalComponent = targetComponents.stream().findFirst();
        if (optionalComponent.isPresent()) {
            RootComponent hostingComponent = optionalComponent.get();
            if (hostingComponent instanceof Compute) {
                return Optional.of((Compute) hostingComponent);
            } else {
                return resolveHostingComputeComponent(graph, hostingComponent);
            }
        } else {
            // Leaf reached
            return Optional.empty();
        }
    }

    public static void resolveChildComponents(Graph<RootComponent, RootRelation> graph, List<RootComponent> children, RootComponent component) {
        Set<RootComponent> targetComponents = getTargetComponents(graph, component, HostedOn.class);
        Optional<RootComponent> optionalComponent = targetComponents.stream().findFirst();
        if (optionalComponent.isPresent()) {
            RootComponent child = optionalComponent.get();
            children.add(child);
            resolveChildComponents(graph, children, child);
        }
    }
}
