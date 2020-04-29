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
    private Map<String, Object> mainMap;
    private Map<String, Object> componentsMap;
    Map<String, Object> componentsTypeMap;
    Map<String, Object> relationsTypeMap;

    public EdmmYamlBuilder (){
        componentsTypeMap = new HashMap<>();
        relationsTypeMap = new HashMap<>();
        componentsMap = new HashMap<>();
        mainMap = new HashMap<>();
    }

    public EdmmYamlBuilder component(Class<? extends RootComponent> componentClass, Class<? extends RootComponent> hostedOnClass) {
        Map<String, Object> componentMap = new HashMap<>();
        Map<String, Object> relationMap = new HashMap<>();
        List<Map> relations = new ArrayList<>();

        relationMap.put("hosted_on", hostedOnClass.getSimpleName());
        relations.add(relationMap);
        componentMap.put("type", TypeResolver.resolve(componentClass));
        componentMap.put("relations", relations);
        componentsMap.put(componentClass.getSimpleName(), componentMap);

        //component(hostedOnClass);
        return this;
    }

    public EdmmYamlBuilder component(Class<? extends RootComponent> componentClass) {
        Map<String, Object> componentMap = new HashMap<>();
        componentMap.put("type", TypeResolver.resolve(componentClass));
        componentsMap.put(componentClass.getSimpleName(), componentMap);
        return this;
    }

    public String buildToYamlString() {
        populateTypeMaps();

        mainMap.put("components", componentsMap);
        mainMap.put("relation_types",relationsTypeMap);
        mainMap.put("component_types", componentsTypeMap);

        return new Yaml().dumpAsMap(mainMap);
    }

    private void populateTypeMaps() {
        // adding the relation_type and component_type section
        Set<String> typeSet = TypeResolver.typeSet();
        for(String type : typeSet) {
            Class<? extends ModelEntity> modelEntity = TypeResolver.resolve(type);
            if (RootComponent.class.isAssignableFrom(modelEntity)) {
                // type is a component type
                Class<? extends RootComponent> rootComponent = modelEntity.asSubclass(RootComponent.class);
                Object extendsType = (RootComponent.class == modelEntity) ? null : TypeResolver.resolve((Class<? extends ModelEntity>) rootComponent.getSuperclass());
                Map<String, Object> extendsMap = new HashMap<>();
                extendsMap.put("extends",extendsType);
                componentsTypeMap.put(type,extendsMap);

            } else if (RootRelation.class.isAssignableFrom(modelEntity)){
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
