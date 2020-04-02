package io.github.edmm.docker;

import java.util.List;

import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.RootRelation;
import lombok.NonNull;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMultigraph;

public final class DependencyGraph extends DirectedMultigraph<Container, ConnectsTo> {

    private final List<Container> stacks;
    private final Graph<RootComponent, RootRelation> graph;

    public DependencyGraph(@NonNull List<Container> stacks, @NonNull Graph<RootComponent, @NonNull RootRelation> graph) {
        super(ConnectsTo.class);
        this.stacks = stacks;
        this.graph = graph;
        initGraph();
    }

    private void initGraph() {
        stacks.forEach(this::addVertex);
        for (Container sourceStack : stacks) {
            sourceStack.getComponents().forEach(source -> {
                source.getRelations().stream()
                    .filter(r -> r instanceof ConnectsTo)
                    .forEach(r -> {
                        RootComponent target = graph.getEdgeTarget(r);
                        for (Container targetStack : stacks) {
                            if (targetStack.hasComponent(target)) {
                                this.addEdge(sourceStack, targetStack, (ConnectsTo) r);
                            }
                        }
                    });
            });
        }
    }
}
