package io.github.edmm.model.visitor;

import java.util.Set;
import java.util.function.Predicate;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class VisitorHelper {

    private static final Logger logger = LoggerFactory.getLogger(VisitorHelper.class);

    public static void visit(Set<RootComponent> components, ComponentVisitor visitor) {
        visit(components, visitor, component -> true);
    }

    public static void visit(Set<RootComponent> components, ComponentVisitor visitor, @NonNull Predicate<? super RootComponent> filter) {
        components.stream()
                .filter(filter)
                .forEach(component -> {
                    if (!component.isTransformed()) {
                        logger.debug("Visit '{}' object for '{}' component", component.getClass().getSimpleName(), component.getName());
                        component.accept(visitor);
                    }
                });
    }

    public static void visit(Set<RootRelation> relations, RelationVisitor visitor) {
        visit(relations, visitor, relation -> true);
    }

    public static void visit(Set<RootRelation> relations, RelationVisitor visitor, @NonNull Predicate<? super RootRelation> filter) {
        relations.stream()
                .filter(filter)
                .forEach(relation -> {
                    if (!relation.isTransformed()) {
                        Entity component = relation.getEntity()
                                .getParent().orElseThrow(IllegalStateException::new)
                                .getParent().orElseThrow(IllegalStateException::new);
                        logger.debug("Visit '{}' object for '{}' relation of component '{}'", relation.getClass().getSimpleName(), relation.getName(), component.getName());
                        relation.accept(visitor);
                    }
                });
    }
}
