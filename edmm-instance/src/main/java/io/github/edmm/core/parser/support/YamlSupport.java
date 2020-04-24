package io.github.edmm.core.parser.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.Metadata;
import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.edimm.InstanceState;
import io.github.edmm.model.edimm.RelationInstance;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.Property;

import static org.apache.commons.collections4.MapUtils.emptyIfNull;

public class YamlSupport {

    DumperOptions dumperOptions = new DumperOptions();

    public DumperOptions getYamlOptions() {
        this.setDumperOptions();
        return this.dumperOptions;
    }

    private void setDumperOptions() {
        // options for a proper layout of yaml file
        this.dumperOptions.setIndent(2);
        this.dumperOptions.setPrettyFlow(true);
        this.dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    }

    static boolean isFirstPropertyPrioritized(Property p1, Property p2) {
        // id should be on top
        return (isPropertyID(p1) && !isPropertyID(p2))
            || (isPropertyKey(p1) && !isPropertyKey(p2))
            || (isPropertyName(p1) && (!isPropertyName(p2) && !isPropertyID(p2)))
            || (isPropertyType(p1)) && (!isPropertyType(p2) && !isPropertyID(p2) && !isPropertyName(p2))
            || (isPropertyState(p1) && (!isPropertyState(p2) && isPropertyType(p2) && !isPropertyID(p2) && isPropertyName(p2)));
    }

    private static boolean isPropertyID(Property property) {
        return property.getName().contains(YamlConstants.ID);
    }

    private static boolean isPropertyName(Property property) {
        return property.getName().contains(YamlConstants.NAME);
    }

    private static boolean isPropertyKey(Property property) {
        return property.getName().contains(YamlConstants.KEY);
    }

    private static boolean isPropertyType(Property property) {
        return property.getName().contains(YamlConstants.TYPE);
    }

    private static boolean isPropertyState(Property property) {
        return property.getName().contains(YamlConstants.STATE);
    }

    public static List<InstanceProperty> getInstancePropertiesFromYamlContent(Map<String, Object> yamlContent) {
        List<InstanceProperty> instanceProperties = new ArrayList<>();

        ((List<Map<String, String>>) yamlContent.get(YamlConstants.INSTANCE_PROPERTIES)).forEach(map -> {
            InstanceProperty instanceProperty = new InstanceProperty(
                map.get(YamlConstants.KEY),
                map.get(YamlConstants.TYPE),
                map.get(YamlConstants.INSTANCE_VALUE));
            instanceProperties.add(instanceProperty);
        });

        return instanceProperties;
    }

    public static List<ComponentInstance> getComponentInstancesFromYamlContent(Map<String, Object> yamlContent) {
        List<ComponentInstance> componentInstances = new ArrayList<>();

        ((List<Map<String, Object>>) yamlContent.get(YamlConstants.COMPONENT_INSTANCES)).forEach(map -> {
            ComponentInstance componentInstance = new ComponentInstance();
            componentInstance.setName(map.get(YamlConstants.NAME) != null ? String.valueOf(map.get(YamlConstants.NAME)) : null);
            componentInstance.setType(map.get(YamlConstants.TYPE) != null ? String.valueOf(map.get(YamlConstants.TYPE)) : null);
            componentInstance.setState(map.get(YamlConstants.STATE) != null ? InstanceState.InstanceStateForComponentInstance.valueOf(String.valueOf(map.get(YamlConstants.STATE))) : null);
            componentInstance.setId(map.get(YamlConstants.ID) != null ? String.valueOf(map.get(YamlConstants.ID)) : null);
            componentInstance.setCreatedAt(map.get(YamlConstants.CREATED_AT) != null ? String.valueOf(map.get(YamlConstants.CREATED_AT)) : null);
            componentInstance.setInstanceProperties(map.get(YamlConstants.INSTANCE_PROPERTIES) != null ? YamlSupport.getInstancePropertiesFromYamlContent(map) : null);
            componentInstance.setDescription(map.get(YamlConstants.DESCRIPTION) != null ? String.valueOf(map.get(YamlConstants.DESCRIPTION)) : null);
            componentInstance.setMetadata(map.get(YamlConstants.METADATA) != null ? Metadata.of(emptyIfNull((Map<String, Object>) map.get(YamlConstants.METADATA))) : null);
            componentInstance.setRelationInstances(map.get(YamlConstants.RELATION_INSTANCES) != null ? YamlSupport.getRelationInstancesFromYamlContent(map) : null);

            componentInstances.add(componentInstance);
        });

        return componentInstances;
    }

    private static List<RelationInstance> getRelationInstancesFromYamlContent(Map<String, Object> yamlContent) {
        List<RelationInstance> relationInstances = new ArrayList<>();

        ((List<Map<String, Object>>) yamlContent.get(YamlConstants.RELATION_INSTANCES)).forEach(map -> {
            RelationInstance relationInstance = new RelationInstance();
            relationInstance.setId(map.get(YamlConstants.ID) != null ? String.valueOf(map.get(YamlConstants.ID)) : null);
            relationInstance.setTargetInstanceId(map.get(YamlConstants.RELATION_TARGET_INSTANCE_ID) != null ? String.valueOf(map.get(YamlConstants.RELATION_TARGET_INSTANCE_ID)) : null);
            relationInstance.setType(map.get(YamlConstants.TYPE) != null ? String.valueOf(map.get(YamlConstants.TYPE)) : null);
            relationInstance.setDescription(map.get(YamlConstants.DESCRIPTION) != null ? String.valueOf(map.get(YamlConstants.DESCRIPTION)) : null);
            relationInstance.setInstanceProperties(map.get(YamlConstants.INSTANCE_PROPERTIES) != null ? YamlSupport.getInstancePropertiesFromYamlContent(emptyIfNull((Map<String, Object>) map.get(YamlConstants.INSTANCE_PROPERTIES))) : null);
            relationInstance.setMetadata(map.get(YamlConstants.METADATA) != null ? Metadata.of(emptyIfNull((Map<String, Object>) map.get(YamlConstants.METADATA))) : null);

            relationInstances.add(relationInstance);
        });
        return relationInstances;
    }
}
