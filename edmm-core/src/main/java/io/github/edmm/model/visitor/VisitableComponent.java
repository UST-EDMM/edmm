package io.github.edmm.model.visitor;

public interface VisitableComponent {

    void accept(ComponentVisitor v);
}
