package io.github.edmm.model.relation;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.visitor.RelationVisitor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class DependsOn extends RootRelation {

    public DependsOn(MappingEntity relationDefinition, MappingEntity entity) {
        super(relationDefinition, entity);
    }

    @Override
    public void accept(RelationVisitor v) {
        v.visit(this);
    }
}
