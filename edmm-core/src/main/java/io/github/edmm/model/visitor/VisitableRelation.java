package io.github.edmm.model.visitor;

public interface VisitableRelation {

    void accept(RelationVisitor v);
}
