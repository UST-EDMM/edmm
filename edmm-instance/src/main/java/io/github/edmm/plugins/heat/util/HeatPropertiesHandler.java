package io.github.edmm.plugins.heat.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.edmm.model.edimm.InstanceProperty;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class HeatPropertiesHandler {

    public static List<InstanceProperty> getDeploymentInstanceProperties(Map<String, String> parameterMap, List<Map<String, Object>> outputList) {
        return Stream.of(
            handleParameterMap(parameterMap),
            handleOutputList(outputList)).flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private static List<InstanceProperty> handleParameterMap(Map<String, String> parameterMap) {
        List<InstanceProperty> deploymentInstanceProperties = new ArrayList<>();
        parameterMap.forEach((key, value) -> deploymentInstanceProperties.add(new InstanceProperty(key, value.getClass().getSimpleName(), value)));
        return deploymentInstanceProperties;
    }

    private static List<InstanceProperty> handleOutputList(List<Map<String, Object>> outputList) {
        List<InstanceProperty> deploymentInstanceProperties = new ArrayList<>();
        emptyIfNull(outputList).forEach(
            entry -> entry.forEach((key, value) -> {
                deploymentInstanceProperties.add(new InstanceProperty(key, key.getClass().getSimpleName(), String.valueOf(value)));
            })
        );
        return deploymentInstanceProperties;
    }
}
