package io.github.edmm.model.component;

import java.util.Optional;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;
import lombok.ToString;

@ToString
public class Platform extends RootComponent {

    public static final Attribute<String> REGION = new Attribute<>("region", String.class);

    public Platform(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public Optional<String> getRegion() {
        return getProperty(REGION);
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}
