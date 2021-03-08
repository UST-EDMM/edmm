package io.github.edmm.model.component;

import java.util.Optional;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;

import lombok.ToString;

@ToString
public class RabbitMq extends Mom {

    public static final Attribute<Integer> MANAGEMENT_PORT = new Attribute<>("management_port", Integer.class);

    public RabbitMq(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public Integer getManagementPort() {
        return getProperty(MANAGEMENT_PORT, 15672);
    }

    public Optional<Integer> getPort() {
        return Optional.of(getProperty(PORT, 5672));
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}
