package io.github.miwurster.edm.model.visitor;

public interface VisitableRelation {

    void accept(RelationVisitor v);
}
