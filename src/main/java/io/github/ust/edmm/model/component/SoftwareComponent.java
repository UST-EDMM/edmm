package io.github.ust.edmm.model.component;

import io.github.ust.edmm.core.parser.MappingEntity;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class SoftwareComponent extends RootComponent {

    public SoftwareComponent(MappingEntity mappingEntity) {
        super(mappingEntity);
    }
}
