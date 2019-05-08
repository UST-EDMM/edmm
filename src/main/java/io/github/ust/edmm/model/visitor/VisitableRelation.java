package io.github.ust.edmm.model.visitor;

public interface VisitableRelation {

    void accept(RelationVisitor v);
}
