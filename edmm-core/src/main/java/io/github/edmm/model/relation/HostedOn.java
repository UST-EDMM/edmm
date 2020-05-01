package io.github.edmm.model.relation;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.visitor.RelationVisitor;

import lombok.ToString;

@ToString
public class HostedOn extends DependsOn {

    public HostedOn(MappingEntity relationDefinition, RootComponent sourceComponent) {
        super(relationDefinition, sourceComponent);
    }

    @Override
    public void accept(RelationVisitor v) {
        v.visit(this);
    }
}
