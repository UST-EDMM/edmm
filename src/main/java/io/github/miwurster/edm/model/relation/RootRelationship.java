package io.github.miwurster.edm.model.relation;

import io.github.miwurster.edm.core.parser.MappingEntity;
import io.github.miwurster.edm.model.Attribute;
import io.github.miwurster.edm.model.ModelEntity;
import io.github.miwurster.edm.model.component.RootComponent;
import io.github.miwurster.edm.model.visitor.RelationVisitor;
import io.github.miwurster.edm.model.visitor.VisitableRelation;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class RootRelationship extends ModelEntity implements VisitableRelation {

    private static Attribute<RootComponent> SOURCE = new Attribute<>("source", RootComponent.class);
    private static Attribute<RootComponent> TARGET = new Attribute<>("target", RootComponent.class);

    public RootRelationship(MappingEntity entity) {
        super(entity);
    }

    @Override
    public void accept(RelationVisitor v) {
        v.visit(this);
    }
}
