package io.github.edmm.model.visitor;

import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.DependsOn;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;

public interface RelationVisitor {

    default void visit(RootRelation relation) {
        // noop
    }

    default void visit(ConnectsTo relation) {
        visit((RootRelation) relation);
    }

    default void visit(DependsOn relation) {
        visit((RootRelation) relation);
    }

    default void visit(HostedOn relation) {
        visit((RootRelation) relation);
    }
}
