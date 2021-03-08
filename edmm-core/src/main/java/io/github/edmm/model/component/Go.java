package io.github.edmm.model.component;

import java.util.Optional;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;

import lombok.ToString;

@ToString
public class Go extends WebServer {

    public static final Attribute<String> WORKDIR = new Attribute<>("workdir", String.class);
    public static final Attribute<String> ENTRYPOINT = new Attribute<>("entrypoint", String.class);

    public Go(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public Optional<String> getWorkdir() {
        return getProperty(WORKDIR);
    }

    public Optional<String> getEntrypoint() {
        return getProperty(ENTRYPOINT);
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}
