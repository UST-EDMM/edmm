package io.github.edmm.model.relation;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.Operation;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.support.ModelEntity;
import io.github.edmm.model.visitor.RelationVisitor;
import io.github.edmm.model.visitor.VisitableRelation;

import lombok.ToString;

@ToString
public class RootRelation extends ModelEntity implements VisitableRelation {

    public static Attribute<String> TARGET = new Attribute<>("target", String.class);

    private final RootComponent sourceComponent;

    public RootRelation(MappingEntity relationDefinition, RootComponent sourceComponent) {
        super(relationDefinition);
        this.sourceComponent = sourceComponent;
    }

    public String getTarget() {
        String type = get(TARGET);
        if (Objects.isNull(type)) {
            throw new IllegalStateException("Relation needs to have a target");
        }
        return type;
    }

    public String getSource() {
        return sourceComponent.getName();
    }

    public StandardLifecycle getStandardLifecycle() {
        return new StandardLifecycle(getOperations());
    }

    @Override
    public void accept(RelationVisitor v) {
        v.visit(this);
    }

    @ToString
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
