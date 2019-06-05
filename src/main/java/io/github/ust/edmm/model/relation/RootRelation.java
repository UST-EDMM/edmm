package io.github.ust.edmm.model.relation;

import java.util.Objects;

import io.github.ust.edmm.core.parser.MappingEntity;
import io.github.ust.edmm.model.support.Attribute;
import io.github.ust.edmm.model.support.ModelEntity;
import io.github.ust.edmm.model.visitor.RelationVisitor;
import io.github.ust.edmm.model.visitor.VisitableRelation;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class RootRelation extends ModelEntity implements VisitableRelation {

    public static Attribute<String> TARGET = new Attribute<>("target", String.class);

    private final MappingEntity entity;

    public RootRelation(MappingEntity relationDefinition, MappingEntity entity) {
        super(relationDefinition);
        this.entity = entity;
    }

    public String getTarget() {
        String type = get(TARGET);
        if (Objects.isNull(type)) {
            throw new IllegalStateException("Relation needs to have a target");
        }
        return type;
    }

    @Override
    public void accept(RelationVisitor v) {
        v.visit(this);
    }
}
