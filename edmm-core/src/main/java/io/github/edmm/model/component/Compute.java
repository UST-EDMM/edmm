package io.github.edmm.model.component;

import java.util.Optional;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class Compute extends RootComponent {

    public static Attribute<String> OS_FAMILY = new Attribute<>("os_family", String.class);
    public static Attribute<String> MACHINE_IMAGE = new Attribute<>("machine_image", String.class);
    public static Attribute<String> INSTANCE_TYPE = new Attribute<>("instance_type", String.class);
    public static Attribute<String> KEY_NAME = new Attribute<>("key_name", String.class);
    public static Attribute<String> PUBLIC_KEY = new Attribute<>("public_key", String.class);

    public Compute(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public Optional<String> getOsFamily() {
        return getProperty(OS_FAMILY);
    }

    public Optional<String> getMachineImage() {
        return getProperty(MACHINE_IMAGE);
    }

    public Optional<String> getInstanceType() {
        return getProperty(INSTANCE_TYPE);
    }

    public Optional<String> getKeyName() {
        return getProperty(KEY_NAME);
    }

    public Optional<String> getPublicKey() {
        return getProperty(PUBLIC_KEY);
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}
