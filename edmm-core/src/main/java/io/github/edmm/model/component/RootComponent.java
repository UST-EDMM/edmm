package io.github.edmm.model.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.Operation;
import io.github.edmm.model.support.TypeWrapper;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.VisitableComponent;
import io.github.edmm.model.Artifact;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.support.ModelEntity;
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

    public StandardLifecycle getStandardLifecycle() {
        return new StandardLifecycle(getOperations());
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

    @ToString
    @EqualsAndHashCode
    public static class StandardLifecycle {

        private final Map<String, Operation> operations;

        public StandardLifecycle(Map<String, Operation> operations) {
            this.operations = operations;
        }

        public Optional<Operation> getCreate() {
            return Optional.ofNullable(operations.get("create"));
        }

        public Optional<Operation> getConfigure() {
            return Optional.ofNullable(operations.get("configure"));
        }

        public Optional<Operation> getStart() {
            return Optional.ofNullable(operations.get("start"));
        }

        public Optional<Operation> getStop() {
            return Optional.ofNullable(operations.get("stop"));
        }

        public Optional<Operation> getDelete() {
            return Optional.ofNullable(operations.get("delete"));
        }
    }
}
