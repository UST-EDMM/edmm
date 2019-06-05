package io.github.ust.edmm.model.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import io.github.ust.edmm.core.parser.Entity;
import io.github.ust.edmm.core.parser.MappingEntity;
import io.github.ust.edmm.model.Artifact;
import io.github.ust.edmm.model.relation.RootRelation;
import io.github.ust.edmm.model.support.Attribute;
import io.github.ust.edmm.model.support.ModelEntity;
import io.github.ust.edmm.model.support.TypeWrapper;
import io.github.ust.edmm.model.visitor.ComponentVisitor;
import io.github.ust.edmm.model.visitor.VisitableComponent;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class RootComponent extends ModelEntity implements VisitableComponent {

    public static final Attribute<String> TYPE = new Attribute<>("type", String.class);
    public static final Attribute<Artifact> ARTIFACTS = new Attribute<>("artifacts", Artifact.class);
    public static final Attribute<RootRelation> RELATIONS = new Attribute<>("relations", RootRelation.class);

    public RootComponent(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public String getType() {
        return get(TYPE);
    }

    public List<Artifact> getArtifacts() {
        List<Artifact> result = new ArrayList<>();
        Optional<Entity> artifactsEntity = getEntity().getChild(ARTIFACTS);
        artifactsEntity.ifPresent(value -> populateArtifacts(result, value));
        return Lists.reverse(result);
    }

    public List<RootRelation> getRelations() {
        List<RootRelation> result = new ArrayList<>();
        Optional<Entity> artifactsEntity = getEntity().getChild(RELATIONS);
        artifactsEntity.ifPresent(value -> populateRelations(result, value));
        return Lists.reverse(result);
    }

    private void populateRelations(List<RootRelation> result, Entity entity) {
        Set<Entity> children = entity.getChildren();
        for (Entity child : children) {
            MappingEntity relationEntity = (MappingEntity) child;

            RootRelation relation = TypeWrapper.wrapRelation(relationEntity, this.entity);
            result.add(relation);
        }
    }

    @Override
    public void accept(ComponentVisitor v) {
        v.visit(this);
    }
}
