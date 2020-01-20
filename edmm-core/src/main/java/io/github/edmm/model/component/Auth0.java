package io.github.edmm.model.component;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;
import lombok.ToString;

@ToString
public class Auth0 extends Saas {

    public static final Attribute<String> DOMAIN = new Attribute<>("domain", String.class);
    public static final Attribute<String> IDENTIFIER = new Attribute<>("identifier", String.class);
    public static final Attribute<String> SCOPES = new Attribute<>("scopes", String.class);

    public Auth0(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public String getDomain() {
        return getProperty(DOMAIN)
                .orElseThrow(() -> new IllegalStateException("Auth0 needs to specify the domain property"));
    }

    public String getIdentifier() {
        return getProperty(IDENTIFIER)
                .orElseThrow(() -> new IllegalStateException("Auth0 needs to specify the identifier property"));
    }

    public String getScopes() {
        return getProperty(SCOPES)
                .orElseThrow(() -> new IllegalStateException("Auth0 needs to specify the scopes property"));
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}
