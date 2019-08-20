package io.github.edmm.model.relation;

import io.github.edmm.core.parser.MappingEntity;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class HostedOn extends DependsOn {

    public HostedOn(MappingEntity relationDefinition, MappingEntity entity) {
        super(relationDefinition, entity);
    }
}
