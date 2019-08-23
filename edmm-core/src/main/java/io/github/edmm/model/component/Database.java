package io.github.edmm.model.component;

import java.util.Optional;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class Database extends RootComponent {

    public static final Attribute<String> SCHEMA_NAME = new Attribute<>("schema_name", String.class);
    public static final Attribute<String> USER = new Attribute<>("user", String.class);
    public static final Attribute<String> PASSWORD = new Attribute<>("password", String.class);

    public Database(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public String getSchemaName() {
        return getProperty(SCHEMA_NAME).orElseThrow(IllegalStateException::new);
    }

    public Optional<String> getUser() {
        return getProperty(USER);
    }

    public Optional<String> getPassword() {
        return getProperty(PASSWORD);
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}
