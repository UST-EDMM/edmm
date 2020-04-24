package io.github.edmm.plugins.heat.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.plugins.heat.model.StackStatus;
import org.openstack4j.model.heat.Resource;

public class HeatResourceHandler {
    protected static List<InstanceProperty> getResourceInstanceProperties(Resource resource, Map<String, Map<String, Object>> allResourceContent) {
        List<InstanceProperty> instanceProperties = new ArrayList<>();
        Map<String, Object> resourceMap = allResourceContent.get(resource.getResourceName());
        // get property map of resource
        Map<String, Object> propertiesMap = (Map<String, Object>) resourceMap.get(HeatConstants.PROPERTIES);
        // iterate over key-value pairs of property map
        propertiesMap.forEach((key, value) -> {
            // openstack sets tags and metadata in property map, but we handle them differently
            if (key.equals(HeatConstants.METADATA) || key.equals(HeatConstants.TAGS)) {
                return;
            }
            // value may be of type string or list, check both cases and handle accordingly
            if (value instanceof String) {
                InstanceProperty instanceProperty = new InstanceProperty(key, value.getClass().getSimpleName(), String.valueOf(value));
                instanceProperties.add(instanceProperty);
            } else if (value instanceof List) {
                // entries of list may be of type string or map, check both cases and handle accordingly
                if (((List) value).get(0) instanceof String) {
                    ((List<String>) value).forEach(entry -> {
                        InstanceProperty instanceSubProperty = new InstanceProperty(key, entry.getClass().getSimpleName(), entry);
                        instanceProperties.add(instanceSubProperty);
                    });
                } else if (((List) value).get(0) instanceof Map) {
                    ((List<Map<String, String>>) value).forEach(entry -> entry.forEach((pKey, pValue) -> {
                        InstanceProperty instanceSubProperty = new InstanceProperty(key + "::" + pKey, pValue.getClass().getSimpleName(), pValue);
                        instanceProperties.add(instanceSubProperty);
                    }));
                }
            }
        });
        return instanceProperties;
    }

    /**
     * Get component instance from list of stack resources.
     *
     * @param resources       all resources of a stack
     * @param resource        resource we want to transform to EDiMM component instance
     * @param resourceContent content of resource
     * @return transformed component instance
     */
    public static ComponentInstance getComponentInstance(List<? extends Resource> resources, Resource resource, Map<String, Map<String, Object>> resourceContent) {
        ComponentInstance componentInstance = new ComponentInstance();

        componentInstance.setType(resource.getType());
        componentInstance.setId(resource.getPhysicalResourceId());
        componentInstance.setCreatedAt(String.valueOf(resource.getTime()));
        componentInstance.setName(resource.getResourceName());
        componentInstance.setState(StackStatus.StackStatusForComponentInstance.valueOf(resource.getResourceStatus()).toEDIMMComponentInstanceState());
        componentInstance.setInstanceProperties(HeatResourceHandler.getResourceInstanceProperties(resource, resourceContent));
        componentInstance.setRelationInstances(HeatRelationHandler.getRelationInstances(resources, resourceContent, resource));
        componentInstance.setMetadata(HeatMetadataHandler.getMetadata(resource, resourceContent));

        return componentInstance;
    }
}
