package io.github.ust.edmm.model.component;

import java.util.HashSet;
import java.util.Set;

import io.github.ust.edmm.core.parser.MappingEntity;
import io.github.ust.edmm.model.Artifact;
import io.github.ust.edmm.model.Attribute;
import io.github.ust.edmm.model.ModelEntity;
import io.github.ust.edmm.model.visitor.ComponentVisitor;
import io.github.ust.edmm.model.visitor.VisitableComponent;
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
