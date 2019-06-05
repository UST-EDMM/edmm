package io.github.ust.edmm.model.component;

import java.util.Optional;

import io.github.ust.edmm.core.parser.MappingEntity;
import io.github.ust.edmm.model.Property;
import io.github.ust.edmm.model.support.Attribute;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class Compute extends RootComponent {

    public static Attribute<String> MACHINE_IMAGE = new Attribute<>("machine_image", String.class);
    public static Attribute<String> OS_FAMILY = new Attribute<>("os_family", String.class);

    public Compute(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public Optional<String> getMachineImage() {
        Optional<Property> property = getProperty(MACHINE_IMAGE.getName());
        return property.map(Property::getValue);
    }

    public Optional<String> getOsFamily() {
        Optional<Property> property = getProperty(OS_FAMILY.getName());
        return property.map(Property::getValue);
    }
}
