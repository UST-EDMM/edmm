package io.github.ust.edmm.model.component;

import io.github.ust.edmm.core.parser.MappingEntity;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class WebApplication extends RootComponent {

    public WebApplication(MappingEntity mappingEntity) {
        super(mappingEntity);
    }
}
