package io.github.edmm.model;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.DescribableElement;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class Operation extends DescribableElement {

    private final MappingEntity componentEntity;

    public Operation(MappingEntity operationDefinition, MappingEntity componentEntity) {
        super(operationDefinition);
        this.componentEntity = componentEntity;
    }
}
