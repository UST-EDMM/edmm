package io.github.edmm.plugins.heat.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.plugins.heat.model.StackStatus;
import io.github.edmm.util.CastUtil;

import org.openstack4j.model.heat.Resource;

public class HeatResourceHandler {

    private static final int firstEntryIndex = 0;
    private static final String propertyKeyDelimiter = "::";

    public static List<ComponentInstance> getComponentInstances(List<? extends Resource> resources, Map<String, Object> template) {
        List<ComponentInstance> componentInstances = new ArrayList<>();
        resources.forEach(resource -> {
            Map<String, Object> resourceContent = CastUtil.safelyCastToStringObjectMap(template.get(HeatConstants.RESOURCES));
            componentInstances.add(getComponentInstance(resources, resource, resourceContent));
        });
        return componentInstances;
    }

    private static ComponentInstance getComponentInstance(List<? extends Resource> resources, Resource resource, Map<String, Object> resourceContent) {
        ComponentInstance componentInstance = new ComponentInstance();

        // TODO artifacts
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

    private static List<InstanceProperty> getResourceInstanceProperties(Resource resource, Map<String, Object> allResourceContent) {
        Map<String, Object> resourceMap = HeatMetadataHandler.getResourceMap(allResourceContent, resource.getResourceName());
        Map<String, Object> propertiesMap = HeatMetadataHandler.getPropertiesMap(resourceMap);

        return handleResourceInstanceProperties(propertiesMap);
    }

    private static List<InstanceProperty> handleResourceInstanceProperties(Map<String, Object> propertiesMap) {
        List<InstanceProperty> instanceProperties = new ArrayList<>();

        propertiesMap.forEach((key, value) -> {
            if (isNoResourceInstanceProperty(key)) {
                return;
            }
            instanceProperties.addAll(Objects.requireNonNull(handleResourceInstanceProperty(key, value)));
        });
        return instanceProperties;
    }

    private static List<InstanceProperty> handleResourceInstanceProperty(String key, Object value) {
        if (value instanceof String) {
            return Collections.singletonList(handleStringProperty(key, String.valueOf(value)));
        } else if (value instanceof List) {
            return handleListProperty(key, (List) value);
        }
        return Collections.emptyList();
    }

    private static InstanceProperty handleStringProperty(String key, String value) {
        return new InstanceProperty(key, value.getClass().getSimpleName(), value);
    }

    private static List<InstanceProperty> handleListProperty(String key, List<?> value) {
        List<InstanceProperty> instanceProperties = new ArrayList<>();

        if (value.get(firstEntryIndex) instanceof String) {
            value.forEach(entry -> instanceProperties.add(handleStringProperty(key, String.valueOf(entry))));
        } else if (value.get(firstEntryIndex) instanceof Map) {
            value.forEach(entry -> CastUtil.safelyCastToStringStringMap(entry).forEach((pKey, pValue) -> instanceProperties.add(handleStringProperty(key + propertyKeyDelimiter + pKey, pValue))));
        }
        return instanceProperties;
    }

    private static boolean isNoResourceInstanceProperty(String key) {
        return key.equals(HeatConstants.METADATA) || key.equals(HeatConstants.TAGS);
    }
}
