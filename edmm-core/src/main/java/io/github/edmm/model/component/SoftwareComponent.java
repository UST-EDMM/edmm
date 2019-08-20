package io.github.edmm.model.component;

import io.github.edmm.core.parser.MappingEntity;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class SoftwareComponent extends RootComponent {

    public SoftwareComponent(MappingEntity mappingEntity) {
        super(mappingEntity);
    }
}
