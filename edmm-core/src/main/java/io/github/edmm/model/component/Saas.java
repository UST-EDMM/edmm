package io.github.edmm.model.component;

import java.util.Optional;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;
import lombok.ToString;

@ToString
public class Saas extends Platform {

    public static final Attribute<String> CLIENT_ID = new Attribute<>("client_id", String.class);
    public static final Attribute<String> CLIENT_SECRET = new Attribute<>("client_secret", String.class);

    public Saas(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public Optional<String> getClientId() {
        return getProperty(CLIENT_ID);
    }

    public Optional<String> getClientSecret() {
        return getProperty(CLIENT_SECRET);
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}
