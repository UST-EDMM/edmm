package io.github.miwurster.edm.model.visitor;

import io.github.miwurster.edm.model.relation.ConnectsTo;
import io.github.miwurster.edm.model.relation.DependsOn;
import io.github.miwurster.edm.model.relation.HostedOn;
import io.github.miwurster.edm.model.relation.RootRelationship;

public interface RelationVisitor {

    default void visit(RootRelationship relation) {
        // noop;
    }

    default void visit(DependsOn relation) {
        // noop
    }

    default void visit(HostedOn relation) {
        // noop
    }

    default void visit(ConnectsTo relation) {
        // noop
    }
}
