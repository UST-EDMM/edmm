package io.github.miwurster.edm.model.visitor;

import io.github.miwurster.edm.model.component.Compute;
import io.github.miwurster.edm.model.component.RootComponent;

public interface ComponentVisitor {

    default void visit(RootComponent component) {
        // noop
    }

    default void visit(Compute component) {
        // noop
    }
}
