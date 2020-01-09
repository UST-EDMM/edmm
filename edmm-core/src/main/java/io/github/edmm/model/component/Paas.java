package io.github.edmm.model.component;

import java.util.Optional;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;
import lombok.ToString;

@ToString
public class Paas extends Platform {

    public static final Attribute<String> ARCHETYPE = new Attribute<>("archetype", String.class);
    public static final Attribute<Integer> MIN_INSTANCES = new Attribute<>("min_instances", Integer.class);
    public static final Attribute<Integer> MAX_INSTANCES = new Attribute<>("max_instances", Integer.class);

    public Paas(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public String getArchetype() {
        return getProperty(ARCHETYPE)
                .orElseThrow(() -> new IllegalStateException("Paas needs to specify the archetype property"));
    }

    public Optional<Integer> getMinInstances() {
        return getProperty(MIN_INSTANCES);
    }

    public Optional<Integer> getMaxInstances() {
        return getProperty(MAX_INSTANCES);
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}
