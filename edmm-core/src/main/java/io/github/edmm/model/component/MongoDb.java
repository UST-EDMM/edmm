package io.github.edmm.model.component;

import java.util.Optional;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;

import lombok.ToString;

@ToString
public class MongoDb extends Dbms {

    public static final Attribute<String> ROOT_USER = new Attribute<>("root_user", String.class);

    public MongoDb(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public Optional<String> getRootUser() {
        return getProperty(ROOT_USER);
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}
