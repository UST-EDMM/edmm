package io.github.edmm.model.relation;

import io.github.edmm.core.parser.MappingEntity;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class DependsOn extends RootRelation {

    public DependsOn(MappingEntity relationDefinition, MappingEntity entity) {
        super(relationDefinition, entity);
    }
}
