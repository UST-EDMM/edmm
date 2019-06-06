package io.github.ust.edmm.model.relation;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.github.ust.edmm.core.parser.MappingEntity;
import io.github.ust.edmm.model.Operation;
import io.github.ust.edmm.model.support.Attribute;
import io.github.ust.edmm.model.support.ModelEntity;
import io.github.ust.edmm.model.visitor.RelationVisitor;
import io.github.ust.edmm.model.visitor.VisitableRelation;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class RootRelation extends ModelEntity implements VisitableRelation {

    public static Attribute<String> TARGET = new Attribute<>("target", String.class);

    private final MappingEntity entity;

    public RootRelation(MappingEntity relationDefinition, MappingEntity entity) {
        super(relationDefinition);
        this.entity = entity;
    }

    public String getTarget() {
        String type = get(TARGET);
        if (Objects.isNull(type)) {
            throw new IllegalStateException("Relation needs to have a target");
        }
        return type;
    }

    public StandardLifecycle getStandardLifecycle() {
        return new StandardLifecycle(getOperations());
    }

    @Override
    public void accept(RelationVisitor v) {
        v.visit(this);
    }

    @ToString
    @EqualsAndHashCode
    public static class StandardLifecycle {

        private final Map<String, Operation> operations;

        public StandardLifecycle(Map<String, Operation> operations) {
            this.operations = operations;
        }

        public Optional<Operation> getPreConfigureSource() {
            return Optional.ofNullable(operations.get("pre_configure_source"));
        }

        public Optional<Operation> getPreConfigureTarget() {
            return Optional.ofNullable(operations.get("pre_configure_target"));
        }

        public Optional<Operation> getPostConfigureSource() {
            return Optional.ofNullable(operations.get("post_configure_source"));
        }

        public Optional<Operation> getPostConfigureTarget() {
            return Optional.ofNullable(operations.get("post_configure_target"));
        }

        public Optional<Operation> getTargetChanged() {
            return Optional.ofNullable(operations.get("target_changed"));
        }

        public Optional<Operation> getTargetRemoved() {
            return Optional.ofNullable(operations.get("target_removed"));
        }
    }
}
