package io.github.ust.edmm.model.relation;

import io.github.ust.edmm.core.parser.MappingEntity;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class DependsOn extends RootRelation {

    public DependsOn(MappingEntity entity) {
        super(entity);
    }
}
