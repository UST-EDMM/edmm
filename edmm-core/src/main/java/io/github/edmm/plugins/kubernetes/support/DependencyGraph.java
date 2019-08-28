package io.github.edmm.plugins.kubernetes.support;

import java.util.List;

import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.plugins.kubernetes.model.ComponentStack;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMultigraph;

public class DependencyGraph extends DirectedMultigraph<ComponentStack, ConnectsTo> {

    private final List<ComponentStack> stacks;
    private final Graph<RootComponent, RootRelation> graph;

    public DependencyGraph(List<ComponentStack> stacks, Graph<RootComponent, RootRelation> graph) {
        super(ConnectsTo.class);
        this.stacks = stacks;
        this.graph = graph;
        initGraph();
    }

    private void initGraph() {
        stacks.forEach(this::addVertex);
        for (ComponentStack sourceStack : stacks) {
            sourceStack.getComponents().forEach(source -> {
                source.getRelations().stream()
                        .filter(r -> r instanceof ConnectsTo)
                        .forEach(r -> {
                            RootComponent target = graph.getEdgeTarget(r);
                            for (ComponentStack targetStack : stacks) {
                                if (targetStack.hasComponent(target)) {
                                    this.addEdge(sourceStack, targetStack, (ConnectsTo) r);
                                }
                            }
                        });
            });
        }
    }
}
