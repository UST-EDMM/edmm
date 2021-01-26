package io.github.edmm.plugins.multi.graph;

import io.github.edmm.model.orchestration.Group;

import org.jgrapht.graph.DefaultEdge;

public class OrderRelation extends DefaultEdge {

    public Group getSource() {
        return (Group) super.getSource();
    }

    public Group getTarget() {
        return (Group) super.getTarget();
    }
}
