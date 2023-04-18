package io.github.edmm.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.DeploymentTechnologyDescriptor;
import io.github.edmm.model.DiscoveryPluginDescriptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Util {

    private final static Logger logger = LoggerFactory.getLogger(Util.class);

    public static String readFromFile(String path) {
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            return readFromStream(fileInputStream);
        } catch (IOException e) {
            logger.error("Error while retrieving contents of the file located at: {}", path);
        }
        return "";
    }

    public static String readFromStream(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        bufferedReader.lines().forEach(str -> stringBuilder.append(str).append("\n"));

        return stringBuilder.toString();
    }

    public static void updateDeploymentTechnologiesInServiceTemplate(TServiceTemplate serviceTemplate,
                                                                     ObjectMapper objectMapper,
                                                                     List<DeploymentTechnologyDescriptor> deploymentTechnologies) {
        try {
            TTag updatedTag = new TTag.Builder(
                Constants.TAG_DEPLOYMENT_TECHNOLOGIES,
                objectMapper.writeValueAsString(deploymentTechnologies)
            ).build();
            List<TTag> serviceTemplateTags = Optional.ofNullable(serviceTemplate.getTags()).orElseGet(() -> {
                List<TTag> tags = new ArrayList<>();
                serviceTemplate.setTags(tags);
                return tags;
            });
            serviceTemplateTags.removeIf(tTag -> Objects.equals(tTag.getName(), Constants.TAG_DEPLOYMENT_TECHNOLOGIES));
            serviceTemplateTags.add(updatedTag);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not write terraform deployment technology to JSON string");
        }
    }

    public static List<DeploymentTechnologyDescriptor> extractDeploymentTechnologiesFromServiceTemplate(
        TServiceTemplate serviceTemplate, ObjectMapper objectMapper) {
        return Optional.ofNullable(serviceTemplate.getTags())
            .flatMap(tTags -> tTags.stream()
                .filter(tTag -> Objects.equals(tTag.getName(), Constants.TAG_DEPLOYMENT_TECHNOLOGIES))
                .findAny())
            .map(TTag::getValue)
            .map(s -> {
                CollectionType collectionType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, DeploymentTechnologyDescriptor.class);
                try {
                    return objectMapper.<List<DeploymentTechnologyDescriptor>>readValue(s, collectionType);
                } catch (JsonProcessingException e) {
                    throw new TransformationException("Deployment technologies tag could not be parsed as JSON", e);
                }
            })
            .orElseGet(ArrayList::new);
    }

    public static void updateDiscoveryPluginsInServiceTemplate(TServiceTemplate serviceTemplate,
                                                               ObjectMapper objectMapper,
                                                               List<DiscoveryPluginDescriptor> discoveryPlugins) {
        try {
            TTag updatedTag = new TTag.Builder(
                Constants.TAG_DISCOVERY_PLUGINS,
                objectMapper.writeValueAsString(discoveryPlugins)
            ).build();
            List<TTag> serviceTemplateTags = Optional.ofNullable(serviceTemplate.getTags()).orElseGet(() -> {
                List<TTag> tags = new ArrayList<>();
                serviceTemplate.setTags(tags);
                return tags;
            });
            serviceTemplateTags.removeIf(tTag -> Objects.equals(tTag.getName(), Constants.TAG_DISCOVERY_PLUGINS));
            serviceTemplateTags.add(updatedTag);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not write terraform deployment technology to JSON string");
        }
    }

    public static List<DiscoveryPluginDescriptor> extractDiscoveryPluginsFromServiceTemplate(TServiceTemplate serviceTemplate,
                                                                                             ObjectMapper objectMapper) {
        return Optional.ofNullable(serviceTemplate.getTags())
            .flatMap(tTags -> tTags.stream()
                .filter(tTag -> Objects.equals(tTag.getName(), Constants.TAG_DISCOVERY_PLUGINS))
                .findAny())
            .map(TTag::getValue)
            .map(s -> {
                CollectionType collectionType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, DiscoveryPluginDescriptor.class);
                try {
                    return objectMapper.<List<DiscoveryPluginDescriptor>>readValue(s, collectionType);
                } catch (JsonProcessingException e) {
                    throw new TransformationException("Deployment technologies tag could not be parsed as JSON", e);
                }
            })
            .orElseGet(ArrayList::new);
    }

    public static void populateNodeTemplateProperties(TNodeTemplate nodeTemplate,
                                                      Map<String, String> additionalProperties) {
        if (nodeTemplate.getProperties() != null && nodeTemplate.getProperties() instanceof TEntityTemplate.WineryKVProperties properties) {
            properties.getKVProperties()
                .entrySet()
                .stream()
                .filter(entry -> !additionalProperties.containsKey(entry.getKey()) || additionalProperties.get(entry.getKey())
                    .isEmpty())
                .forEach(entry -> additionalProperties.put(entry.getKey(),
                    entry.getValue() != null && !entry.getValue()
                        .isEmpty() ? entry.getValue() : "get_input: " + entry.getKey() + "_" + nodeTemplate.getId()
                        .replaceAll("(\\s)|(:)|(\\.)", "_")));
        }

        // workaround to set new properties
        TEntityTemplate.WineryKVProperties wineryKVProperties = new TEntityTemplate.WineryKVProperties();
        wineryKVProperties.setKVProperties(new LinkedHashMap<>(additionalProperties));
        nodeTemplate.setProperties(wineryKVProperties);
    }

    public static void setStateRunning(TNodeTemplate nodeTemplate) {
        TEntityTemplate.Properties dockerEngineProps = nodeTemplate.getProperties();
        if (dockerEngineProps == null) {
            dockerEngineProps = new TEntityTemplate.WineryKVProperties();
            nodeTemplate.setProperties(dockerEngineProps);
        }
        if (dockerEngineProps instanceof TEntityTemplate.WineryKVProperties props) {
            props.getKVProperties().put("State", "Running");
        }
    }
}
