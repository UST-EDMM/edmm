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

import lombok.Getter;
import org.jgrapht.Graph;

public class RuleAssessor {
    private final BiPredicate<ModelEntity,ModelEntity> similarity;
    private final BiPredicate<ModelEntity,ModelEntity> equality;

    /**
     * With unsupportedComponent we mean a component that belongs to a sub graph matching the expected topology.
     * if the matching sub graph is: ComponentA -- hosted_on --> ComponentB
     * both ComponentA and ComponentB are considered unsupportedComponents even if they are supported in other kinds of topologies
     */
    private Set<RootComponent> actualUnsupportedComponents;

    private Graph<RootComponent,RootRelation> expectedTopology;
    private Graph<RootComponent,RootRelation> actualTopology;

    private Set<RootComponent> expectedComponents;
    private Set<RootRelation> expectedRelations;

    public RuleAssessor () {
        similarity = new ModelEntitySimilarity();
        equality = new ModelEntityEquality();
    }

    /**
     * @param expectedModel the model derived from the topology given by the rule.
     * @param actualModel the model of the topology drawn by the user.
     * @param exactAssessment True, if we want to check for exact match (see RuleAssessor.ModelEntityEquality)
     *                        i.e Auth0 does not match exactly with Saas.
     *                        False, if we want a more relaxed match (see RuleAssessor.ModelEntitySimilarity)
     *                        i.e AwsBeanstalk matches with Paas.
     * @param currentComponent the component that is being visited
     *
     * @return True, if, in the neighbourhood of the currentComponent (in the actual topology), there is
     *              a sub graph matching the expected topology.
     */
    public RuleAssessor.Result assess(
        DeploymentModel expectedModel,
        DeploymentModel actualModel,
        RootComponent currentComponent,
        boolean exactAssessment
    ) {
        actualUnsupportedComponents = new HashSet<>();
        expectedTopology = expectedModel.getTopology();
        actualTopology = actualModel.getTopology();
        // Getting the nodes in the expected topology that are similar to the unsupported node.
        // Our search in the graph will start from this nodes
        List<RootComponent> candidates = new ArrayList<>();
        for (RootComponent c : expectedTopology.vertexSet()) {
            if (exactAssessment) {
                if (equality.test(c, currentComponent)) {
                    candidates.add(c);
                }
            } else {
                if (similarity.test(c, currentComponent)) {
                    candidates.add(c);
                }
            }
        }

        // For each candidate we check if there is a topology containing that candidate and
        // similar to the topology specified by the plugin developer.
        // If it exists then we have found a match and the rule can be applied
        boolean match = false;
        for (RootComponent candidate : candidates) {
            // every time we find a match of type component -> relation -> component we delete this elements from the sets below
            expectedComponents = new HashSet<>(expectedTopology.vertexSet());
            expectedRelations = new HashSet<>(expectedTopology.edgeSet());

            expectedComponents.remove(candidate);
            // currentComponent matches with candidate so it is considered as a not supported component in the actual topology
            actualUnsupportedComponents.add(currentComponent);
            // we first check exact matches i.e  expected Auth0 - actual Auth0
            checkGraph(currentComponent, candidate,equality);

            if (!exactAssessment) {
                // then we search similar matches i.e expected PaaS - actual AwsBeanstalk
                checkGraph(currentComponent, candidate,similarity);
            }

            // once the sets are empty it means that there is a match
            if (expectedComponents.size() == 0 && expectedRelations.size() == 0) {
                match = true;
                break;
            }
        }

        return new RuleAssessor.Result(match, actualUnsupportedComponents);
    }

    private void checkGraph(
        RootComponent currentComponent,
        RootComponent candidate,
        BiPredicate<ModelEntity,ModelEntity> replace
    ) {
        Set<RootComponent> expectedVisited = new HashSet<>();
        Set<RootComponent> actualVisited = new HashSet<>();
        Queue<RootComponent> expectedToVisit = new LinkedList<>();
        Queue<RootComponent> actualToVisit = new LinkedList<>();
        expectedToVisit.add(candidate);
        actualToVisit.add(currentComponent);

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

                        // actualSource and actualTarget match in the expectedTopology so they are
                        // considered has not supported components
                        actualUnsupportedComponents.add(actualSource);
                        actualUnsupportedComponents.add(actualTarget);

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

    public class Result {
        private final boolean match;
        @Getter
        private final List<RootComponent> unsupportedComponents;

        public Result(boolean match, Set<RootComponent> components) {
            this.match = match;
            unsupportedComponents = (match) ? new ArrayList<>(components) : null;
        }
        public boolean matches() { return this.match; }
    }
}
