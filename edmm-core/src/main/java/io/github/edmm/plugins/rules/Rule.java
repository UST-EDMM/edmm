package io.github.edmm.plugins.rules;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.support.ModelEntity;

import lombok.Getter;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMultigraph;

public abstract class Rule implements Comparable<Rule> {
    @Getter
    protected String name;

    @Getter
    protected String description;

    @Getter
    protected Integer priority;

    public Rule(String name, String description, int priority) {
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    public Rule(String name, String description) {
        this.name = name;
        this.description = description;
        this.priority = Integer.MAX_VALUE - 1;
    }

    public boolean evaluate(DeploymentModel actualMode,RootComponent unsupportedComponent) {
        Graph<RootComponent, RootRelation> subTopology = new DirectedMultigraph<>(RootRelation.class);
        subTopology.addVertex(unsupportedComponent);

        return evaluate(actualMode,subTopology);
    }

    public boolean evaluate(DeploymentModel actualModel,Graph<RootComponent, RootRelation> actualSubTopology) {
        String yaml = fromTopology();
        DeploymentModel expectedModel = DeploymentModel.of(yaml);

        return compareTopologies(expectedModel,actualModel,actualSubTopology);
    }

    private boolean compareTopologies(
        DeploymentModel expectedModel,
        DeploymentModel actualModel,
        Graph<RootComponent, RootRelation> actualSubTopology
    ) {
        Graph<RootComponent, RootRelation> expectedSubTopology = expectedModel.getTopology();

        Set<RootRelation> actualRelations = new HashSet<>(actualSubTopology.edgeSet());
        for (RootRelation expectedRelation : expectedSubTopology.edgeSet()) {
            for (RootRelation actualRelation : actualRelations) {

                if (areEqual(expectedRelation,actualRelation)) {

                    Optional<RootComponent> expectedTarget = expectedModel.getComponent(expectedRelation.getTarget());
                    Optional<RootComponent> actualTarget = actualModel.getComponent(actualRelation.getTarget());

                    Optional<RootComponent> expectedSource = expectedModel.getComponent(expectedRelation.getSource());
                    Optional<RootComponent> actualSource = actualModel.getComponent(actualRelation.getSource());

                    if (expectedTarget.isPresent() && actualTarget.isPresent() &&
                        expectedSource.isPresent() && actualSource.isPresent() &&
                        areSimilar(expectedTarget.get(), actualTarget.get()) &&
                        areSimilar(expectedSource.get(), actualSource.get())) {

                        actualRelations.remove(actualRelation);
                        break;
                    }
                }
            }
        }
        Set<RootComponent> actualComponents = new HashSet<>(actualSubTopology.vertexSet());
        for (RootComponent expectedComponent : expectedSubTopology.vertexSet()) {
            actualComponents.removeIf(actualComponent -> areSimilar(expectedComponent, actualComponent));
        }

        return actualRelations.size() == 0 && actualComponents.size() == 0;
    }

    private boolean areEqual(ModelEntity expected, ModelEntity actual) {
        return expected.getClass() == actual.getClass();
    }

    private boolean areSimilar(ModelEntity expected, ModelEntity actual) {
        Class<? extends ModelEntity> expectedClass = expected.getClass();
        Class<? extends ModelEntity> actualClass = actual.getClass();
        return expectedClass == actualClass || expectedClass == actualClass.getSuperclass();
    }

    protected abstract String fromTopology();

    protected abstract String toTopology();

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Rule rule) {
        return getPriority().compareTo(rule.getPriority());
    }
}
