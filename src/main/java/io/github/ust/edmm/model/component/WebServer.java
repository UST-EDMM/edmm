package io.github.ust.edmm.model.component;

import java.util.Optional;

import io.github.ust.edmm.core.parser.MappingEntity;
import io.github.ust.edmm.model.support.Attribute;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class WebServer extends SoftwareComponent {

    public static Attribute<Integer> PORT = new Attribute<>("port", Integer.class);

    public WebServer(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public Optional<Integer> getPort() {
        return getProperty(PORT);
    }
}
