package io.github.edmm.plugins.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiPredicate;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.support.ModelEntity;

import org.jgrapht.Graph;

public class RuleAssessor {
    private final Graph<RootComponent,RootRelation> expectedTopology;
    private final Graph<RootComponent,RootRelation> actualTopology;
    private final BiPredicate<ModelEntity,ModelEntity> similarity;
    private final BiPredicate<ModelEntity,ModelEntity> equality;

    private Set<RootComponent> expectedComponents;
    private Set<RootRelation> expectedRelations;

    /**
     * @param expectedModel the model derived from the topology given by the rule
     * @param actualModel the model of the topology drawn by the user
     */
    public RuleAssessor (DeploymentModel expectedModel, DeploymentModel actualModel) {
        expectedTopology = expectedModel.getTopology();
        actualTopology = actualModel.getTopology();

        similarity = new ModelEntitySimilarity();
        equality = new ModelEntityEquality();
    }

    public boolean assess(RootComponent unsupportedComponent) {
        // Getting the nodes in the expected topology that are similar to the unsupported node.
        // Our search in the graph will start from this nodes
        List<RootComponent> candidates = new ArrayList<>();
        for (RootComponent c : expectedTopology.vertexSet()) {
            if (similarity.test(c, unsupportedComponent)) {
                candidates.add(c);
            }
        }

        // For each candidate we check if there is a topology containing that candidate and
        // similar to the topology specified by the plugin developer.
        // If it exists then we have found a match and the rule can be applied
        boolean match = false;
        for (RootComponent current : candidates) {
            // every time we find a match of type component -> relation -> component we delete this elements from the sets below
            expectedComponents = new HashSet<>(expectedTopology.vertexSet());
            expectedRelations = new HashSet<>(expectedTopology.edgeSet());

            expectedComponents.remove(current);
            // we first check exact matches i.e  expected Auth0 - actual Auth0
            checkGraph(unsupportedComponent,current,equality);
            // then we search similar matches i.e expected PaaS - actual AwsBeanstalk
            checkGraph(unsupportedComponent,current,similarity);

            // once the sets are empty it means that there is a match
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

        // the graph is navigated popping out the nodes from the to-visit-queue
        while (expectedToVisit.size() > 0) {
            RootComponent currentExpected = expectedToVisit.remove();
            RootComponent currentActual = actualToVisit.remove();

            // we compare the edges incoming to the current node in the expected topology
            // with the edges incoming to the current node in the actual topology
            Set<RootRelation> incomingRelationsExpected = expectedTopology.incomingEdgesOf(currentExpected);
            Set<RootRelation> incomingRelationsActual = actualTopology.incomingEdgesOf(currentActual);
            checkEdges(incomingRelationsExpected,incomingRelationsActual,replace, expectedToVisit, actualToVisit);

            // now we do the same as above, but with the outgoing edges
            Set<RootRelation> outgoingRelationsExpected = expectedTopology.outgoingEdgesOf(currentExpected);
            Set<RootRelation> outgoingRelationsActual = actualTopology.outgoingEdgesOf(currentActual);
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
                    // the relation matches, now we check the source and the target of the edge
                    RootComponent expectedSource = expectedTopology.getEdgeSource(expectedEdge);
                    RootComponent expectedTarget = expectedTopology.getEdgeTarget(expectedEdge);
                    RootComponent actualSource = actualTopology.getEdgeSource(actualEdge);
                    RootComponent actualTarget = actualTopology.getEdgeTarget(actualEdge);

                    if (replace.test(expectedSource, actualSource) &&
                        replace.test(expectedTarget, actualTarget)) {
                        // found match: source-component -> relation -> target-component

                        expectedRelations.remove(expectedEdge);
                        expectedComponents.remove(expectedSource);
                        expectedComponents.remove(expectedTarget);

                        // it's important to add the nodes in the same order, so that when they are removed
                        // we do not invert sources with targets
                        expectedToVisit.add(expectedSource);
                        actualToVisit.add(actualSource);

                        expectedToVisit.add(expectedTarget);
                        actualToVisit.add(actualTarget);
                    }
                }
            }
        }
    }

    /**
     * two model entities are equal if they have the same class
     */
    public static class ModelEntityEquality implements BiPredicate<ModelEntity,ModelEntity> {
        @Override
        public boolean test(ModelEntity expected, ModelEntity actual) {
            return expected.getClass() == actual.getClass();
        }
    }

    /**
     * two model entities are similar if they are equal or if the already present model-entity's superclass
     * is equal to the expected entity's class
     */
    public static class ModelEntitySimilarity implements BiPredicate<ModelEntity,ModelEntity> {
        @Override
        public boolean test(ModelEntity expected, ModelEntity actual) {
            Class<? extends ModelEntity> expectedClass = expected.getClass();
            Class<? extends ModelEntity> actualClass = actual.getClass();
            return expectedClass == actualClass || expectedClass == actualClass.getSuperclass();
        }
    }
}
