package io.github.edmm.model.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.support.GraphHelper;
import io.github.edmm.model.Operation;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.support.ModelEntity;
import io.github.edmm.model.support.TypeWrapper;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.VisitableComponent;
import lombok.ToString;

@ToString
public class RootComponent extends ModelEntity implements VisitableComponent {

    public static final Attribute<String> TYPE = new Attribute<>("type", String.class);
    public static final Attribute<RootRelation> RELATIONS = new Attribute<>("relations", RootRelation.class);

    private final List<RootRelation> relationCache = new ArrayList<>();

    public RootComponent(MappingEntity mappingEntity) {
        super(mappingEntity);
        // Resolve the chain of types
        EntityGraph graph = entity.getGraph();
        MappingEntity typeRef = GraphHelper.findTypeEntity(graph, entity).
            orElseThrow(() -> new IllegalStateException("A component must be an instance of an existing type"));
        List<MappingEntity> typeChain = GraphHelper.resolveInheritanceChain(graph, typeRef);
        typeChain.forEach(this::updateEntityChain);
    }

    public String getType() {
        return get(TYPE);
    }

    public List<RootRelation> getRelations() {
        if (relationCache.isEmpty()) {
            List<RootRelation> result = new ArrayList<>();
            Optional<Entity> artifactsEntity = getEntity().getChild(RELATIONS);
            artifactsEntity.ifPresent(value -> populateRelations(result, value));
            relationCache.addAll(Lists.reverse(result));
        }
        return relationCache;
    }

    public boolean hasRelations() {
        return getRelations().size() > 0;
    }

    public boolean hasOperations() {
        return getOperations().size() > 0;
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
