package io.github.edmm.core.yaml.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.edimm.RelationInstance;
import io.github.edmm.util.CastUtil;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.Property;

public class YamlSupport {

    private final DumperOptions dumperOptions = new DumperOptions();

    static boolean isFirstPropertyPrioritized(Property p1, Property p2) {
        // id should be on top
        return (isPropertyID(p1) && !isPropertyID(p2))
            || (isPropertyKey(p1) && !isPropertyKey(p2))
            || (isPropertyName(p1) && (!isPropertyName(p2) && !isPropertyID(p2)))
            || (isPropertyType(p1)) && (!isPropertyType(p2) && !isPropertyID(p2) && !isPropertyName(p2))
            || (isPropertyState(p1) && (!isPropertyState(p2) && !isPropertyType(p2) && !isPropertyID(p2) && !isPropertyName(p2)));
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

        CastUtil.safelyCastToObjectList(yamlContent.get(YamlConstants.INSTANCE_PROPERTIES)).forEach(map -> {
            Map<String, Object> stringObjectMap = CastUtil.safelyCastToStringObjectMap(map);
            InstanceProperty instanceProperty = new InstanceProperty(
                String.valueOf(stringObjectMap.get(YamlConstants.KEY)),
                String.valueOf(stringObjectMap.get(YamlConstants.TYPE)),
                stringObjectMap.get(YamlConstants.INSTANCE_VALUE));
            instanceProperties.add(instanceProperty);
        });

        return instanceProperties;
    }

    public static List<ComponentInstance> getComponentInstancesFromYamlContent(Map<String, Object> yamlContent) {
        List<ComponentInstance> componentInstances = new ArrayList<>();

        CastUtil.safelyCastToObjectList(yamlContent.get(YamlConstants.COMPONENT_INSTANCES)).forEach(componentYamlContent -> {
            ComponentInstance componentInstance = ComponentInstance.ofYamlContent(CastUtil.safelyCastToStringObjectMap(componentYamlContent));
            componentInstances.add(componentInstance);
        });

        return componentInstances;
    }

    public static List<RelationInstance> getRelationInstancesFromYamlContent(Map<String, Object> yamlContent) {
        List<RelationInstance> relationInstances = new ArrayList<>();

        CastUtil.safelyCastToObjectList(yamlContent.get(YamlConstants.RELATION_INSTANCES)).forEach(relationYamlContent -> {
            RelationInstance relationInstance = RelationInstance.ofYamlContent(CastUtil.safelyCastToStringObjectMap(relationYamlContent));
            relationInstances.add(relationInstance);
        });
        return relationInstances;
    }

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
}
