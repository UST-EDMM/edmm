package io.github.edmm.plugins.heat.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.plugins.heat.model.StackStatus;
import org.openstack4j.model.heat.Resource;

public class HeatResourceHandler {

    private static final int firstEntryIndex = 0;
    private static final String propertyKeyDelimiter = "::";

    protected static List<InstanceProperty> getResourceInstanceProperties(Resource resource, Map<String, Map<String, Object>> allResourceContent) {
        Map<String, Object> resourceMap = HeatMetadataHandler.getResourceMap(allResourceContent, resource.getResourceName());
        Map<String, Object> propertiesMap = HeatMetadataHandler.getPropertiesMap(resourceMap);

        List<InstanceProperty> instanceProperties = handleResourceInstanceProperties(propertiesMap);

        return instanceProperties;
    }

    private static List<InstanceProperty> handleResourceInstanceProperties(Map<String, Object> propertiesMap) {
        List<InstanceProperty> instanceProperties = new ArrayList<>();

        propertiesMap.forEach((key, value) -> {
            if (isNoResourceInstanceProperty(key)) {
                return;
            }
            handleResourceInstanceProperty(key, value).stream().forEach(instanceProperty -> instanceProperties.add(instanceProperty));
        });
        return instanceProperties;
    }

    private static List<InstanceProperty> handleResourceInstanceProperty(String key, Object value) {
        if (value instanceof String) {
            return Arrays.asList(handleStringProperty(key, String.valueOf(value)));
        } else if (value instanceof List) {
            return handleListProperty(key, (List) value);
        }
        return null;
    }

    private static InstanceProperty handleStringProperty(String key, String value) {
        return new InstanceProperty(key, value.getClass().getSimpleName(), value);
    }

    private static List<InstanceProperty> handleListProperty(String key, List value) {
        List<InstanceProperty> instanceProperties = new ArrayList<>();

        if (value.get(firstEntryIndex) instanceof String) {
            value.forEach(entry -> instanceProperties.add(handleStringProperty(key, String.valueOf(entry))));
        } else if (value.get(firstEntryIndex) instanceof Map) {
            ((List<Map<String, String>>) value).forEach(entry -> entry.forEach((pKey, pValue) -> {
                instanceProperties.add(handleStringProperty(key + propertyKeyDelimiter + pKey, pValue));
            }));
        }
        return instanceProperties;
    }

    private static boolean isNoResourceInstanceProperty(String key) {
        return key.equals(HeatConstants.METADATA) || key.equals(HeatConstants.TAGS);
    }

    public static ComponentInstance getComponentInstance(List<? extends Resource> resources, Resource resource, Map<String, Map<String, Object>> resourceContent) {
        ComponentInstance componentInstance = new ComponentInstance();

        componentInstance.setType(resource.getType());
        componentInstance.setId(resource.getPhysicalResourceId());
        componentInstance.setCreatedAt(String.valueOf(resource.getTime()));
        componentInstance.setName(resource.getResourceName());
        componentInstance.setState(StackStatus.StackStatusForComponentInstance.valueOf(resource.getResourceStatus()).toEDIMMComponentInstanceState());
        componentInstance.setInstanceProperties(HeatResourceHandler.getResourceInstanceProperties(resource, resourceContent));
        componentInstance.setRelationInstances(HeatRelationHandler.getRelationInstances(resources, resourceContent, resource));
        componentInstance.setMetadata(HeatMetadataHandler.getComponentMetadata(resource, resourceContent));

        return componentInstance;
    }
}
