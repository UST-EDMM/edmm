package io.github.edmm.model.component;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;
import lombok.ToString;

@ToString
public class Dbaas extends Platform {

    public static final Attribute<String> INSTANCE_TYPE = new Attribute<>("instance_type", String.class);

    public Dbaas(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public String getInstanceType() {
        return getProperty(INSTANCE_TYPE)
                .orElseThrow(() -> new IllegalStateException("Dbaas needs to specify the instance_type property"));
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}
