package io.github.ust.edmm.model.relation;

import io.github.ust.edmm.core.parser.MappingEntity;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class ConnectsTo extends RootRelation {

    public ConnectsTo(MappingEntity relationDefinition, MappingEntity entity) {
        super(relationDefinition, entity);
    }
}
