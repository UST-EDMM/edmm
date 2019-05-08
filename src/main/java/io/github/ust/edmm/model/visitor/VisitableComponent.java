package io.github.ust.edmm.model.visitor;

public interface VisitableComponent {

    void accept(ComponentVisitor v);
}
