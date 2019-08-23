package io.github.edmm.model.visitor;

import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.DependsOn;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;

public interface RelationVisitor {

    default void visit(ConnectsTo relation) {
        // noop
    }

    default void visit(DependsOn relation) {
        // noop
    }

    default void visit(HostedOn relation) {
        // noop
    }

    default void visit(RootRelation relation) {
        // noop;
    }
}
