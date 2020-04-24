package io.github.edmm.plugins.heat.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.Metadata;
import org.openstack4j.model.heat.Resource;

import static org.apache.commons.collections4.MapUtils.emptyIfNull;

public class HeatMetadataHandler {
    /**
     * Get metadata of resource.
     *
     * @param resource        openstack heat resource
     * @param resourceContent content of openstack heat resource, i.e. properties
     * @return EDiMM metadata object with all metadata for resource
     */
    protected static Metadata getMetadata(Resource resource, Map<String, Map<String, Object>> resourceContent) {
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
