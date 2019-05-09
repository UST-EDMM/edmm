package io.github.ust.edmm.model.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.github.ust.edmm.core.parser.Entity;
import io.github.ust.edmm.core.parser.EntityGraph;
import io.github.ust.edmm.core.parser.MappingEntity;
import io.github.ust.edmm.core.parser.support.GraphHelper;
import io.github.ust.edmm.model.Property;
import io.github.ust.edmm.model.support.Attribute;
import io.github.ust.edmm.model.support.DescribableElement;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class ModelEntity extends DescribableElement {

    public static Attribute<String> EXTENDS = new Attribute<>("extends", String.class);
    public static Attribute<Property> PROPERTIES = new Attribute<>("properties", Property.class);

    public ModelEntity(MappingEntity entity) {
        super(entity);
    }

    public Optional<String> getExtends() {
        return Optional.ofNullable(get(EXTENDS));
    }

    public Map<String, Property> getProperties() {
        EntityGraph graph = entity.getGraph();
        Map<String, Property> result = new HashMap<>();
        // Resolve the chain of types
        MappingEntity typeRef = GraphHelper.findTypeEntity(graph, entity).
                orElseThrow(() -> new IllegalStateException("A component must be an instance of an existing type"));
        List<MappingEntity> typeChain = GraphHelper.resolveInheritanceChain(graph, typeRef);
        // Create property objects for all available assignments
        Optional<Entity> propertiesEntity = entity.getChild(PROPERTIES);
        propertiesEntity.ifPresent(value -> populateProperties(result, value));
        // Update current map by property definitions
        for (MappingEntity typeEntity : typeChain) {
            propertiesEntity = typeEntity.getChild("properties");
            propertiesEntity.ifPresent(value -> populateProperties(result, value));
        }
        return result;
    }

    public Optional<Property> getProperty(String name) {
        return Optional.ofNullable(getProperties().get(name));
    }

    private void populateProperties(Map<String, Property> result, Entity entity) {
        Set<Entity> children = entity.getChildren();
        for (Entity child : children) {
            MappingEntity propertyEntity = (MappingEntity) child;
            Property property = new Property(propertyEntity, this.entity);
            result.put(property.getName(), property);
        }
    }
}
