package io.github.edmm.plugins.kubernetes.model;

import java.util.Map;

import io.github.edmm.core.TransformationHelper;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.docker.Container;
import io.github.edmm.model.Property;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigMapResource implements KubernetesResource {

    private static final Logger logger = LoggerFactory.getLogger(ServiceResource.class);

    private final Container stack;
    private final Map<String, Property> properties;

    private ConfigMap configMap;

    public ConfigMapResource(Container stack, Map<String, Property> properties) {
        this.stack = stack;
        this.properties = properties;
    }

    @Override
    public void build() {
        ConfigMapBuilder builder = new ConfigMapBuilder()
            .withNewMetadata()
            .withName(getName())
            .endMetadata();
        for (var p : properties.entrySet()) {
            if (TransformationHelper.matchesBlacklist(p.getKey())) {
                continue;
            }
            builder.addToData(p.getKey().toUpperCase(), p.getValue().getValue());
        }
        configMap = builder.build();
    }

    @Override
    public String toYaml() {
        if (configMap == null) {
            throw new TransformationException("Resource not yet built, call build() first");
        }
        try {
            return SerializationUtils.dumpAsYaml(configMap);
        } catch (JsonProcessingException e) {
            logger.error("Failed to dump YAML", e);
            throw new TransformationException(e);
        }
    }

    @Override
    public String getName() {
        return stack.getConfigMapName();
    }
}
