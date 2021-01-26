package io.github.edmm.plugins.multi.graph;

import java.util.LinkedList;

import io.github.edmm.model.orchestration.Group;

import org.jgrapht.graph.DefaultDirectedGraph;

public class GroupProvisioningOrderGraph extends DefaultDirectedGraph<Group, OrderRelation> {

    public GroupProvisioningOrderGraph() {
        super(OrderRelation.class);
    }

    public void removeAllEdges(GroupProvisioningOrderGraph graph) {
        LinkedList<OrderRelation> copy = new LinkedList<OrderRelation>();
        for (OrderRelation e : graph.edgeSet()) {
            copy.add(e);
        }
        graph.removeAllEdges(copy);
    }

    public void clearGraph() {
        removeAllEdges(this);
        removeAllVertices(this);
    }

    public void removeAllVertices(GroupProvisioningOrderGraph graph) {
        LinkedList<Group> copy = new LinkedList<Group>();
        for (Group v : graph.vertexSet()) {
            copy.add(v);
        }
        graph.removeAllVertices(copy);
    }
}
