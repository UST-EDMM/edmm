package io.github.edmm.core.plugin;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;
import org.jgrapht.Graph;

public abstract class GraphHelper {

    /**
     * Find the leaf components in the graph (components that have no outgoing edges).
     *
     * @param graph The graph to search
     * @return mutable snapshot of all leaf vertices.
     */
    public static Set<RootComponent> getLeafComponents(Graph<RootComponent, RootRelation> graph) {
        Set<RootComponent> vertexSet = graph.vertexSet();
        Set<RootComponent> leaves = new HashSet<>(vertexSet.size() * 2);
        for (RootComponent vertex : vertexSet) {
            if (graph.outgoingEdgesOf(vertex).isEmpty()) {
                leaves.add(vertex);
            }
        }
        return leaves;
    }

    /**
     * Fetch all of the dependents of the given target component.
     *
     * @return mutable snapshot of the source components of all incoming edges
     */
    public static Set<RootComponent> getSourceComponents(Graph<RootComponent, RootRelation> graph, RootComponent target, Class<? extends RootRelation> clazz) {
        Set<RootRelation> edges = graph.incomingEdgesOf(target);
        Set<RootComponent> sources = new LinkedHashSet<>();
        for (RootRelation edge : edges) {
            if (clazz.isInstance(edge)) {
                sources.add(graph.getEdgeSource(edge));
            }
        }
        return sources;
    }

    /**
     * Fetch all of the dependencies of the given source components.
     *
     * @return mutable snapshot of the target components of all outgoing edges
     */
    public static Set<RootComponent> getTargetComponents(Graph<RootComponent, RootRelation> graph, RootComponent source, Class<? extends RootRelation> clazz) {
        Set<RootRelation> edges = graph.outgoingEdgesOf(source);
        Set<RootComponent> targets = new LinkedHashSet<>();
        for (RootRelation edge : edges) {
            if (clazz.isInstance(edge)) {
                targets.add(graph.getEdgeTarget(edge));
            }
        }
        return targets;
    }

    /**
     * Find a certain component in the graph.
     *
     * @param graph     The graph to search
     * @param component The component to find
     * @return optional {@link RootComponent}
     */
    public static Optional<RootComponent> getComponent(Graph<RootComponent, RootRelation> graph, RootComponent component) {
        return graph.vertexSet()
                .stream()
                .filter(c -> c.equals(component)).findFirst();
    }

    /**
     * Find all components of a certain type in the graph.
     *
     * @param graph The graph to search
     * @param clazz The type of component to search for
     * @param <T>   subclass of {@link RootComponent}
     * @return list of components
     */
    @SuppressWarnings("unchecked")
    public static <T extends RootComponent> List<T> getComponents(Graph<RootComponent, RootRelation> graph, Class<T> clazz) {
        return (List<T>) graph.vertexSet()
                .stream()
                .filter(clazz::isInstance)
                .collect(Collectors.toList());
    }

    /**
     * Find all relations of a certain type in the graph.
     *
     * @param graph The graph to search
     * @param clazz The type of component to search for
     * @param <T>   subclass of {@link RootComponent}
     * @return list of relations
     */
    @SuppressWarnings("unchecked")
    public static <T extends RootRelation> List<T> getRelations(Graph<RootComponent, RootRelation> graph, Class<T> clazz) {
        return (List<T>) graph.edgeSet()
                .stream()
                .filter(clazz::isInstance)
                .collect(Collectors.toList());
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
}
