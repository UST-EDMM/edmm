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

    /**
     * Get DumperOptions for dumping YAMl file.
     *
     * @return the options for snakeyaml
     */
    public static DumperOptions getYamlOptions() {
        DumperOptions options = new DumperOptions();
        // options for a proper layout of yaml file
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return options;
    }

    /**
     * Helper method to compare object properties to order output of Java Objects in YAML file.
     *
     * @param p1 first property to compare
     * @param p2 second property to compare
     */
    static boolean isPrioritized(Property p1, Property p2) {
        // id should be on top
        if ((p1.getName().contains(YamlConstants.ID) && !p2.getName().contains(YamlConstants.ID))
            // name property should appear after id but before anything else
            || (p1.getName().contains(YamlConstants.NAME) && (!p2.getName().contains(YamlConstants.NAME) && !p2.getName().contains(YamlConstants.ID)))
            // key should appear on top in properties
            || (p1.getName().contains(YamlConstants.KEY) && !p2.getName().contains(YamlConstants.KEY))
            // type should appear after id and name
            || (p1.getName().contains(YamlConstants.TYPE) && (!p2.getName().contains(YamlConstants.TYPE) && !p2.getName().contains(YamlConstants.ID) && !p2.getName().contains(YamlConstants.NAME)))
            // state should appear after type, id, and name
            || (p1.getName().contains(YamlConstants.STATE) && (!p2.getName().contains(YamlConstants.STATE) && !p2.getName().contains(YamlConstants.TYPE) && !p2.getName().contains(YamlConstants.ID) && !p2.getName().contains(YamlConstants.NAME)))) {
            return true;
        }
        // default no prioritization
        return false;
    }

    /**
     * Get instance properties for a deployment instance from YAML file.
     *
     * @param yamlContent map of content of input YAML
     * @return list of instance properties
     */
    public static List<InstanceProperty> getInstanceProperties(Map<String, Object> yamlContent) {
        List<InstanceProperty> instanceProperties = new ArrayList<>();

        ((List<Map<String, String>>) yamlContent.get(YamlConstants.INSTANCE_PROPERTIES)).forEach(map -> {
            InstanceProperty instanceProperty = new InstanceProperty(
                map.get(YamlConstants.KEY) != null ? map.get(YamlConstants.KEY) : null,
                map.get(YamlConstants.TYPE) != null ? map.get(YamlConstants.TYPE) : null,
                map.get(YamlConstants.INSTANCE_VALUE) != null ? map.get(YamlConstants.INSTANCE_VALUE) : null);
            instanceProperties.add(instanceProperty);
        });

        return instanceProperties;
    }

    /**
     * Get component instances for a deployment instance from YAML file.
     *
     * @param yamlContent map of content of input YAML
     * @return list of component instances
     */
    public static List<ComponentInstance> getComponentInstances(Map<String, Object> yamlContent) {
        List<ComponentInstance> componentInstances = new ArrayList<>();

        ((List<Map<String, Object>>) yamlContent.get(YamlConstants.COMPONENT_INSTANCES)).forEach(map -> {
            ComponentInstance componentInstance = new ComponentInstance();
            componentInstance.setName(map.get(YamlConstants.NAME) != null ? String.valueOf(map.get(YamlConstants.NAME)) : null);
            componentInstance.setType(map.get(YamlConstants.TYPE) != null ? String.valueOf(map.get(YamlConstants.TYPE)) : null);
            componentInstance.setState(map.get(YamlConstants.STATE) != null ? InstanceState.InstanceStateForComponentInstance.valueOf(String.valueOf(map.get(YamlConstants.STATE))) : null);
            componentInstance.setId(map.get(YamlConstants.ID) != null ? String.valueOf(map.get(YamlConstants.ID)) : null);
            componentInstance.setCreatedAt(map.get(YamlConstants.CREATED_AT) != null ? String.valueOf(map.get(YamlConstants.CREATED_AT)) : null);
            componentInstance.setInstanceProperties(map.get(YamlConstants.INSTANCE_PROPERTIES) != null ? YamlSupport.getInstanceProperties(map) : null);
            componentInstance.setDescription(map.get(YamlConstants.DESCRIPTION) != null ? String.valueOf(map.get(YamlConstants.DESCRIPTION)) : null);
            componentInstance.setMetadata(map.get(YamlConstants.METADATA) != null ? Metadata.of(emptyIfNull((Map<String, Object>) map.get(YamlConstants.METADATA))) : null);
            componentInstance.setRelationInstances(map.get(YamlConstants.RELATION_INSTANCES) != null ? YamlSupport.getRelationInstances(map) : null);

            componentInstances.add(componentInstance);
        });

        return componentInstances;
    }

    /**
     * Get relation instances of a component instance from YAML file.
     *
     * @param yamlContent map of content of input YAML
     * @return list of relation instances of a component instance
     */
    public static List<RelationInstance> getRelationInstances(Map<String, Object> yamlContent) {
        List<RelationInstance> relationInstances = new ArrayList<>();

        ((List<Map<String, Object>>) yamlContent.get(YamlConstants.RELATION_INSTANCES)).forEach(map -> {
            RelationInstance relationInstance = new RelationInstance();
            relationInstance.setId(map.get(YamlConstants.ID) != null ? String.valueOf(map.get(YamlConstants.ID)) : null);
            relationInstance.setTargetInstanceId(map.get(YamlConstants.RELATION_TARGET_INSTANCE_ID) != null ? String.valueOf(map.get(YamlConstants.RELATION_TARGET_INSTANCE_ID)) : null);
            relationInstance.setType(map.get(YamlConstants.TYPE) != null ? String.valueOf(map.get(YamlConstants.TYPE)) : null);
            relationInstance.setDescription(map.get(YamlConstants.DESCRIPTION) != null ? String.valueOf(map.get(YamlConstants.DESCRIPTION)) : null);
            relationInstance.setInstanceProperties(map.get(YamlConstants.INSTANCE_PROPERTIES) != null ? YamlSupport.getInstanceProperties(emptyIfNull((Map<String, Object>) map.get(YamlConstants.INSTANCE_PROPERTIES))) : null);
            relationInstance.setMetadata(map.get(YamlConstants.METADATA) != null ? Metadata.of(emptyIfNull((Map<String, Object>) map.get(YamlConstants.METADATA))) : null);

            relationInstances.add(relationInstance);
        });
        return relationInstances;
    }
}
