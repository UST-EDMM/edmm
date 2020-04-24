package io.github.edmm.plugins.heat.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.edimm.InstanceProperty;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class HeatPropertiesHandler {
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
}
