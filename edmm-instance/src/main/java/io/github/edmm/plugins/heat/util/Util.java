package io.github.edmm.plugins.heat.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.Metadata;
import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.model.edimm.InstanceProperty;
import io.github.edmm.model.edimm.RelationInstance;
import io.github.edmm.plugins.heat.model.StackStatus;
import org.openstack4j.model.heat.Resource;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;

public class Util {

    /**
     * Retrieve instance properties from OpenStack resource.
     *
     * @param resource           resource to get properties from
     * @param allResourceContent map with content/info of resource
     * @return list of converted EDiMM instance properties
     */
    public static List<InstanceProperty> getResourceInstanceProperties(Resource resource, Map<String, Map<String, Object>> allResourceContent) {
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
     * Get instance properties of OpenStack HEAT stack
     *
     * @param parameterMap parameters of OpenStack HEAT stack
     * @return list of converted EDiMM instance properties
     */
    public static List<InstanceProperty> getDeploymentInstanceProperties(Map<String, String> parameterMap, List<Map<String, Object>> outputList) {
        List<InstanceProperty> deploymentInstanceProperties = new ArrayList<>();
        // iterate over key-value-pairs of properties
        parameterMap.forEach((key, value) -> {
            InstanceProperty instanceProperty = new InstanceProperty(key, value.getClass().getSimpleName(), value);
            deploymentInstanceProperties.add(instanceProperty);
        });
        emptyIfNull(outputList).forEach(
            entry -> entry.forEach((key, value) -> {
                InstanceProperty instanceProperty = new InstanceProperty(key, key.getClass().getSimpleName(), String.valueOf(value));
                deploymentInstanceProperties.add(instanceProperty);
            })
        );

        return deploymentInstanceProperties;
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
        componentInstance.setInstanceProperties(Util.getResourceInstanceProperties(resource, resourceContent));
        componentInstance.setRelationInstances(Util.getRelationInstances(resources, resourceContent, resource));
        componentInstance.setMetadata(Util.getMetadata(resource, resourceContent));

        return componentInstance;
    }

    /**
     * Get list of EDiMM relations for OpenStack HEAT resource.
     *
     * @param resources       resources of stack
     * @param resourceContent resource contents of template with info about resources, e.g. properties, relations...
     * @param resourceInput   resource we try to retrieve relations for
     * @return list of converted EDiMM relation instances
     */
    public static List<RelationInstance> getRelationInstances(List<? extends Resource> resources, Map<String, Map<String, Object>> resourceContent, Resource resourceInput) {
        List<String> dependsOnList = (List<String>) resourceContent.get(resourceInput.getResourceName()).get(HeatConstants.DEPENDS_ON);
        List<RelationInstance> relationInstances = new ArrayList<>();

        emptyIfNull(dependsOnList).forEach(dependsOnResource -> {
            Integer relationCount = 0;
            RelationInstance relationInstance = new RelationInstance();
            relationInstance.setType(HeatConstants.DEPENDS_ON);
            relationInstance.setTargetInstanceId(resources.stream().filter(res -> res.getResourceName().equals(dependsOnResource)).findFirst().get().getPhysicalResourceId());
            relationInstance.setId(HeatConstants.DEPENDS_ON + String.valueOf(relationCount));
            relationInstances.add(relationInstance);
            relationCount++;
        });
        // only return list if not empty, else return null
        return (relationInstances.size() > 0) ? relationInstances : null;
    }

    /**
     * Get metadata of resource.
     *
     * @param resource        openstack heat resource
     * @param resourceContent content of openstack heat resource, i.e. properties
     * @return EDiMM metadata object with all metadata for resource
     */
    public static Metadata getMetadata(Resource resource, Map<String, Map<String, Object>> resourceContent) {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        Map<String, Object> resourceMap = resourceContent.get(resource.getResourceName());
        // get property map of current resource
        Map<String, Object> propertiesMap = (Map<String, Object>) resourceMap.get(HeatConstants.PROPERTIES);

        // get tags of property map
        List<String> tagList = (List<String>) propertiesMap.get(HeatConstants.TAGS);
        // get metadata of property map
        Map<String, Object> metadataMap = (Map<String, Object>) propertiesMap.get(HeatConstants.METADATA);

        // add metadata and tags to result map
        emptyIfNull(metadataMap).forEach(resultMap::put);
        if (tagList != null && !tagList.isEmpty()) {
            resultMap.put(HeatConstants.TAGS, tagList);
        }
        if (resultMap.isEmpty()) {
            return null;
        }
        return Metadata.of(resultMap);
    }

    /**
     * Get metadata of openstack stack.
     *
     * @param tagList     list of tags of stack
     * @param timeoutTime timeout value of stack
     * @param updatedTime updated time value of stack
     * @return EDiMM metadata object with all metadata of stack
     */
    public static Metadata getMetadata(List<String> tagList, Long timeoutTime, String updatedTime) {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        if (timeoutTime != null) {
            resultMap.put(HeatConstants.TIMEOUT, timeoutTime);
        }
        if (updatedTime != null) {
            resultMap.put(HeatConstants.UPDATED_TIME, updatedTime);
        }
        if (tagList != null && !tagList.isEmpty()) {
            resultMap.put(HeatConstants.TAGS, tagList);
        }
        if (resultMap.isEmpty()) {
            return null;
        }
        return Metadata.of(resultMap);
    }
}
