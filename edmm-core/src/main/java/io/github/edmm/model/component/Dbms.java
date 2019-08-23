package io.github.edmm.model.component;

import java.util.Optional;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class Dbms extends SoftwareComponent {

    public static final Attribute<Integer> PORT = new Attribute<>("port", Integer.class);
    public static final Attribute<String> ROOT_PASSWORD = new Attribute<>("root_password", String.class);

    public Dbms(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public Optional<Integer> getPort() {
        return getProperty(PORT);
    }

    public Optional<String> getRootPassword() {
        return getProperty(ROOT_PASSWORD);
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}
