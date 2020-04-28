package io.github.edmm.plugins.heat.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import io.github.edmm.model.Metadata;
import org.openstack4j.model.heat.Resource;

import static java.util.stream.Collectors.toMap;

public class HeatMetadataHandler {

    static Metadata getComponentMetadata(Resource resource, Map<String, Map<String, Object>> resourceContent) {
        Map<String, Object> propertiesMap = getPropertiesMap(getResourceMap(resourceContent, resource.getResourceName()));
        List<String> tagList = getTagList(propertiesMap);
        Map<String, Object> metadataMap = getMetadataMap(propertiesMap);

        Map<String, Object> resultMap = Stream.of(
            handleTagList(tagList).entrySet(),
            handleMetadataMap(metadataMap).entrySet()
        ).flatMap(Set::stream)
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        return resultMap.isEmpty() ? Metadata.of(Collections.emptyMap()) : Metadata.of(resultMap);
    }

    private static Map<String, Object> handleTagList(List<String> tagList) {
        Map<String, Object> tagMap = new LinkedHashMap<>();
        if (tagList != null && !tagList.isEmpty()) {
            tagMap.put(HeatConstants.TAGS, tagList);
        }
        return tagMap;
    }

    private static Map<String, Object> handleMetadataMap(Map<String, Object> metadataMap) {
        if (metadataMap == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> metadataResult = new LinkedHashMap<>();
        metadataMap.forEach(metadataResult::put);
        return metadataResult;
    }

    static Map<String, Object> getResourceMap(Map<String, Map<String, Object>> resourceContent, String resourceName) {
        if (resourceContent.get(resourceName) == null) {
            return Collections.emptyMap();
        }
        return resourceContent.get(resourceName);
    }

    static Map<String, Object> getPropertiesMap(Map<String, Object> resourceContent) {
        if (resourceContent.get(HeatConstants.PROPERTIES) == null) {
            return Collections.emptyMap();
        }
        return (Map<String, Object>) resourceContent.get(HeatConstants.PROPERTIES);
    }

    private static List<String> getTagList(Map<String, Object> propertiesMap) {
        if (propertiesMap == null) {
            return Collections.emptyList();
        }
        return (List<String>) propertiesMap.get(HeatConstants.TAGS);
    }

    private static Map<String, Object> getMetadataMap(Map<String, Object> propertiesMap) {
        if (propertiesMap == null) {
            return Collections.emptyMap();
        }
        return (Map<String, Object>) propertiesMap.get(HeatConstants.METADATA);
    }

    public static Metadata getDeploymentMetadata(List<String> tagList, Long timeoutTime, String updatedTime) {
        Map<String, Object> resultMap = Stream.of(
            handleTagList(tagList).entrySet(),
            handleTimeout(timeoutTime).entrySet(),
            handleUpdatedTime(updatedTime).entrySet()
        ).flatMap(Set::stream)
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        return resultMap.isEmpty() ? Metadata.of(Collections.emptyMap()) : Metadata.of(resultMap);
    }

    private static Map<String, Object> handleTimeout(Long timeoutTime) {
        Map<String, Object> timeoutMap = new LinkedHashMap<>();
        if (timeoutTime != null) {
            timeoutMap.put(HeatConstants.TIMEOUT, timeoutTime);
        }
        return timeoutMap;
    }

    private static Map<String, Object> handleUpdatedTime(String updatedTime) {
        Map<String, Object> updatedTimeMap = new LinkedHashMap<>();
        if (updatedTime != null) {
            updatedTimeMap.put(HeatConstants.UPDATED_TIME, updatedTime);
        }
        return updatedTimeMap;
    }
}
