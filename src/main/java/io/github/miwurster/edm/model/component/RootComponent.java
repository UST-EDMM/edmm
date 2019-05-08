package io.github.miwurster.edm.model.component;

import java.util.HashSet;
import java.util.Set;

import io.github.miwurster.edm.core.parser.MappingEntity;
import io.github.miwurster.edm.model.Artifact;
import io.github.miwurster.edm.model.Attribute;
import io.github.miwurster.edm.model.ModelEntity;
import io.github.miwurster.edm.model.visitor.ComponentVisitor;
import io.github.miwurster.edm.model.visitor.VisitableComponent;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class RootComponent extends ModelEntity implements VisitableComponent {

    public static final Attribute<String> TYPE = new Attribute<>("type", String.class);
    public static final Attribute<Artifact> ARTIFACTS = new Attribute<>("artifacts", Artifact.class);

    public RootComponent(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public String getType() {
        return get(TYPE);
    }

    public Set<Artifact> getArtifacts() {
        return new HashSet<>(getCollection(ARTIFACTS));
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}
