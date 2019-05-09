package io.github.ust.edmm.model.visitor;

import io.github.ust.edmm.model.relation.ConnectsTo;
import io.github.ust.edmm.model.relation.DependsOn;
import io.github.ust.edmm.model.relation.HostedOn;
import io.github.ust.edmm.model.relation.RootRelation;

public interface RelationVisitor {

    default void visit(RootRelation relation) {
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
