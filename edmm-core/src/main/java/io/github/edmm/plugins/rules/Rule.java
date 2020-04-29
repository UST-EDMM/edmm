package io.github.edmm.plugins.rules;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.support.ModelEntity;
import lombok.Getter;
import org.jgrapht.Graph;

public abstract class Rule implements Comparable<Rule> {
    @Getter
    protected String name;

    @Getter
    protected String description;

    @Getter
    protected Integer priority;

    public Rule(String name, String description, int priority){
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    public Rule(String name, String description){
        this.name = name;
        this.description = description;
        this.priority = Integer.MAX_VALUE - 1;
    }

    public boolean evaluate(RootComponent component) {
        // single component evaluation
        // TODO
        return false;
    }


    public boolean evaluate(DeploymentModel actualModel,Graph<RootComponent, RootRelation> actualSubTopology){
        String yaml = fromTopology();
        DeploymentModel expectedModel = DeploymentModel.of(yaml);

        return compare(expectedModel,actualModel,actualSubTopology);
    }

    private boolean compare(
        DeploymentModel expectedModel,
        DeploymentModel actualModel,
        Graph<RootComponent, RootRelation> actualSubTopology
    ) {
        Graph<RootComponent, RootRelation> expectedSubTopology = expectedModel.getTopology();

        int similarComponentsCount = 0;
        int similarRelationsCount = 0;
        for (RootComponent expectedSource : expectedSubTopology.vertexSet()) {
            for (RootComponent actualSource : actualSubTopology.vertexSet()) {

                if (areSimilar(expectedSource,actualSource)) {
                    // TODO this is not correct if we have more than one component
                    similarComponentsCount += 1;

                    for (RootRelation expectedRelation : expectedSource.getRelations()) {
                        for (RootRelation actualRelation : actualSource.getRelations()) {

                            if (areSimilar(expectedRelation,actualRelation)) {

                                Optional<RootComponent> expectedTarget = expectedModel.getComponent(expectedRelation.getTarget());
                                Optional<RootComponent> actualTarget = actualModel.getComponent(actualRelation.getTarget());

                                if (expectedTarget.isPresent() && actualTarget.isPresent() &&
                                    areSimilar(expectedTarget.get(), actualTarget.get())) {
                                    // TODO this is not correct either
                                    similarRelationsCount += 1;
                                    break; // this might fix the error
                                }
                            }
                        }
                    }
                }
            }
        }

        return similarComponentsCount == expectedSubTopology.vertexSet().size() &&
                similarRelationsCount == expectedSubTopology.edgeSet().size();
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
