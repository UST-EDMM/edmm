package io.github.miwurster.edm.model.visitor;

public interface VisitableComponent {

    void accept(ComponentVisitor v);
}
