package io.github.edmm.model.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.DependsOn;
import io.github.edmm.model.relation.RootRelation;

import org.yaml.snakeyaml.Yaml;

public class EdmmYamlBuilder {
    private final Map<String, Object> mainMap;
    private final Map<String, Object> componentsMap;
    private Map<String, Object> componentsTypeMap;
    private Map<String, Object> relationsTypeMap;

    // this variables are used to record the component that is being modelled
    // once another component will be added, they will be used to add the component to the componentsMap
    // and will be ready again to accept the new component's information
    private Class<? extends RootComponent> currentComponentClass ;
    private List<Map<String, Object>> currentRelations ;

    public EdmmYamlBuilder () {
        componentsTypeMap = new HashMap<>();
        relationsTypeMap = new HashMap<>();
        componentsMap = new HashMap<>();
        mainMap = new HashMap<>();

        currentComponentClass = null;
        currentRelations = new ArrayList<>();
    }

    /**
     *  Adds a component under the 'components' section
     */
    public EdmmYamlBuilder component(Class<? extends RootComponent> componentClass) {
        flushCurrentComponent();
        currentComponentClass = componentClass;
        return this;
    }

    /**
     * Adds 'hosted_on' to the previously specified component
     * @param componentClass the class hosting the component
     */
    public EdmmYamlBuilder hostedOn(Class<? extends RootComponent> componentClass) {
        Map<String, Object> relationMap = new HashMap<>();
        relationMap.put("hosted_on", componentClass.getSimpleName());
        currentRelations.add(relationMap);

        return this;
    }

    /**
     * Adds 'depends_on' to the previously specified component
     * @param componentClass the target class of the relation
     */
    public EdmmYamlBuilder dependsOn(Class<? extends RootComponent> componentClass) {
        Map<String, Object> relationMap = new HashMap<>();
        relationMap.put("depends_on", componentClass.getSimpleName());
        currentRelations.add(relationMap);

        return this;
    }

    /**
     * Adds 'connects_to' to the previously specified component
     * @param componentClass the target class of the relation
     */
    public EdmmYamlBuilder connectsTo(Class<? extends RootComponent> componentClass) {
        Map<String, Object> relationMap = new HashMap<>();
        relationMap.put("connects_to", componentClass.getSimpleName());
        currentRelations.add(relationMap);

        return this;
    }

    public String build() {
        flushCurrentComponent();
        populateTypeMaps();

        mainMap.put("components", componentsMap);
        mainMap.put("relation_types",relationsTypeMap);
        mainMap.put("component_types", componentsTypeMap);

        return new Yaml().dumpAsMap(mainMap);
    }

    public Map<String,Object> getComponentsMap() {
        flushCurrentComponent();
        return this.componentsMap;
    }

    /**
     * It adds the currentComponent and the currentRelations to the componentsMap.
     * This function needs to be called every time a new component is added, so that the currentComponent will be saved
     * and there will be room for the new one.
     * It is also called at build time, to save the last component added.
     */
    private void flushCurrentComponent() {
        if (currentComponentClass != null ) {
            Map<String, Object> componentMap = new HashMap<>();

            if (! currentRelations.isEmpty())
                componentMap.put("relations", currentRelations);
            // here we can add also operations and properties with the same mechanism

            componentMap.put("type", TypeResolver.resolve(currentComponentClass));
            componentsMap.put(currentComponentClass.getSimpleName(), componentMap);
        }
        currentComponentClass = null;
        currentRelations = new ArrayList<>();
    }

    /**
     * This functions populates the  componentsTypeMap and the relationsTypeMap in order to create the
     * 'component_types' and 'relation_types? sections of the yaml in an automatic way
     */
    private void populateTypeMaps() {
        Set<String> typeSet = TypeResolver.typeSet();
        for (String type : typeSet) {
            Class<? extends ModelEntity> modelEntity = TypeResolver.resolve(type);
            if (RootComponent.class.isAssignableFrom(modelEntity)) {
                // type is a component type
                Class<? extends RootComponent> rootComponent = modelEntity.asSubclass(RootComponent.class);
                Object extendsType = (RootComponent.class == modelEntity) ? null : TypeResolver.resolve((Class<? extends ModelEntity>) rootComponent.getSuperclass());
                Map<String, Object> extendsMap = new HashMap<>();
                extendsMap.put("extends",extendsType);
                componentsTypeMap.put(type,extendsMap);

            } else if (RootRelation.class.isAssignableFrom(modelEntity)) {
                // type is a relation type
                Class<? extends RootRelation> rootRelation = modelEntity.asSubclass(RootRelation.class);
                Object extendsType = (DependsOn.class == modelEntity) ? null : TypeResolver.resolve((Class<? extends ModelEntity>) rootRelation.getSuperclass());
                Map<String, Object> extendsMap = new HashMap<>();
                extendsMap.put("extends",extendsType);
                relationsTypeMap.put(type,extendsMap);
            }
        }
    }
}
