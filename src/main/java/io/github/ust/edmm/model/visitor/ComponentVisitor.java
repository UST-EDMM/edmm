package io.github.ust.edmm.model.visitor;

import io.github.ust.edmm.model.component.Compute;
import io.github.ust.edmm.model.component.RootComponent;
import io.github.ust.edmm.model.component.SoftwareComponent;

public interface ComponentVisitor {

    default void visit(RootComponent component) {
        // noop
    }

    default void visit(Compute component) {
        // noop
    }

    default void visit(SoftwareComponent component) {
        // noop
    }
}
