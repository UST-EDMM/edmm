package io.github.edmm.model.component;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.visitor.ComponentVisitor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class WebApplication extends RootComponent {

    public WebApplication(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}
