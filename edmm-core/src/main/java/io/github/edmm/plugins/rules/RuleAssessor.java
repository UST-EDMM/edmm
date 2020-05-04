package io.github.edmm.plugins.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiPredicate;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.support.ModelEntity;

public class RuleAssessor {
    private final DeploymentModel expectedModel;
    private final DeploymentModel actualModel;
    private final BiPredicate<ModelEntity,ModelEntity> similarity;
    private final BiPredicate<ModelEntity,ModelEntity> equality;

    private Set<RootComponent> expectedComponents;
    private Set<RootRelation> expectedRelations;

    public RuleAssessor (DeploymentModel expectedModel, DeploymentModel actualModel) {
        this.expectedModel = expectedModel;
        this.actualModel = actualModel;
        similarity = new ModelEntitySimilarity();
        equality = new ModelEntityEquality();
    }

    public boolean assess(RootComponent unsupportedComponent) {
        // getting the nodes in the expected topology that are similar to the unsupported node
        // our search in the graph will start from this nodes
        List<RootComponent> candidates = new ArrayList<>();
        for (RootComponent c : expectedModel.getTopology().vertexSet()) {
            if (similarity.test(c, unsupportedComponent)) {
                candidates.add(c);
            }
        }

        // for each candidate we check if there is a topology containing that candidate and similar to the topology
        // specified by the plugin developer. If it exists then we have found a match and the rule can be applied
        boolean match = false;
        for (RootComponent current : candidates) {
            // every time we found a match of type component - relation - component we delete this elements from
            // the sets below, once the sets are empty it means that there is a match
            expectedComponents = new HashSet<>(expectedModel.getTopology().vertexSet());
            expectedRelations = new HashSet<>(expectedModel.getTopology().edgeSet());

            expectedComponents.remove(current);
            checkGraph(unsupportedComponent,current,equality);
            checkGraph(unsupportedComponent,current,similarity);

            if (expectedComponents.size() == 0 && expectedRelations.size() == 0) {
                match = true;
                break;
            }
        }

        return match;
    }

    private void checkGraph(
        RootComponent unsupportedComponent,
        RootComponent candidate,
        BiPredicate<ModelEntity,ModelEntity> replace
    ) {
        Set<RootComponent> expectedVisited = new HashSet<>();
        Set<RootComponent> actualVisited = new HashSet<>();
        Queue<RootComponent> expectedToVisit = new LinkedList<>();
        Queue<RootComponent> actualToVisit = new LinkedList<>();
        expectedToVisit.add(candidate);
        actualToVisit.add(unsupportedComponent);

        while (expectedToVisit.size() > 0) {
            RootComponent currentExpected = expectedToVisit.remove();
            RootComponent currentActual = actualToVisit.remove();

            Set<RootRelation> incomingRelationsExpected = expectedModel.getTopology().incomingEdgesOf(currentExpected);
            Set<RootRelation> incomingRelationsActual = actualModel.getTopology().incomingEdgesOf(currentActual);
            checkEdges(incomingRelationsExpected,incomingRelationsActual,replace, expectedToVisit, actualToVisit);

            Set<RootRelation> outgoingRelationsExpected = expectedModel.getTopology().outgoingEdgesOf(currentExpected);
            Set<RootRelation> outgoingRelationsActual = actualModel.getTopology().outgoingEdgesOf(currentActual);
            checkEdges(outgoingRelationsExpected,outgoingRelationsActual,replace, expectedToVisit, actualToVisit);

            expectedVisited.add(currentExpected);
            actualVisited.add(currentActual);
            // we remove the already visited nodes to avoid looping
            expectedToVisit.removeAll(expectedVisited);
            actualToVisit.removeAll(actualVisited);
        }
    }

    private void checkEdges(
        Set<RootRelation> expectedEdges,
        Set<RootRelation> actualEdges,
        BiPredicate<ModelEntity,ModelEntity> replace,
        Queue<RootComponent> expectedToVisit,
        Queue<RootComponent> actualToVisit

    ) {

        for (RootRelation expectedEdge : expectedEdges) {
            for (RootRelation actualEdge : actualEdges) {
                if (replace.test(expectedEdge,actualEdge)) {

                    Optional<RootComponent> expectedTarget = expectedModel.getComponent(expectedEdge.getTarget());
                    Optional<RootComponent> actualTarget = actualModel.getComponent(actualEdge.getTarget());

                    if (expectedTarget.isPresent() && actualTarget.isPresent() &&
                        replace.test(expectedTarget.get(), actualTarget.get())) {

                        expectedRelations.remove(expectedEdge);
                        expectedComponents.remove(expectedTarget.get());

                        expectedToVisit.add(expectedTarget.get());
                        actualToVisit.add(actualTarget.get());
                    }
                }
            }
        }
    }

    public static class ModelEntityEquality implements BiPredicate<ModelEntity,ModelEntity> {
        @Override
        public boolean test(ModelEntity expected, ModelEntity actual) {
            return expected.getClass() == actual.getClass();
        }
    }

    public static class ModelEntitySimilarity implements BiPredicate<ModelEntity,ModelEntity> {
        @Override
        public boolean test(ModelEntity expected, ModelEntity actual) {
            Class<? extends ModelEntity> expectedClass = expected.getClass();
            Class<? extends ModelEntity> actualClass = actual.getClass();
            return expectedClass == actualClass || expectedClass == actualClass.getSuperclass();
        }
    }
}
