package io.github.ust.edmm.model.component;

import java.util.Optional;

import io.github.ust.edmm.core.parser.MappingEntity;
import io.github.ust.edmm.model.Attribute;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class Compute extends RootComponent {

    public static Attribute<String> IP_ADDRESS = new Attribute<>("ip_address", String.class);
    public static Attribute<String> OS_FAMILY = new Attribute<>("os_family", String.class);

    public Compute(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {

    }

    public Optional<String> getIpAddress() {
        return Optional.ofNullable(get(IP_ADDRESS));
    }

    public Optional<String> getOsFamily() {
        return Optional.ofNullable(get(OS_FAMILY));
    }
}
