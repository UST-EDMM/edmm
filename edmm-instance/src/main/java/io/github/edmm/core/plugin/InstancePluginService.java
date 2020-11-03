package io.github.edmm.core.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.edmm.core.transformation.SourceTechnology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InstancePluginService {

    private static final Logger logger = LoggerFactory.getLogger(InstancePluginService.class);

    private final List<InstancePlugin<?>> instancePlugins;

    @Autowired
    public InstancePluginService(List<InstancePlugin<?>> instancePlugins) {
        this.instancePlugins = instancePlugins;
        Map<String, InstancePlugin<?>> pluginMap = new HashMap<>();
        for (InstancePlugin<?> instancePlugin : instancePlugins) {
            if (pluginMap.get(instancePlugin.getSourceTechnology().getId()) != null) {
                logger.error("Found duplicate instance transformation plugin identifier '{}'", instancePlugin.getSourceTechnology().getId());
                throw new IllegalArgumentException("The id '" + instancePlugin.getSourceTechnology().getId() + "' is not unique");
            }
            pluginMap.put(instancePlugin.getSourceTechnology().getId(), instancePlugin);
        }
        logger.debug("Loaded {} instance transformation plugins", instancePlugins.size());
    }

    public List<InstancePlugin<?>> getInstancePlugins() {
        return new ArrayList<>(instancePlugins);
    }

    public Set<SourceTechnology> getSupportedSourceTechnologies() {
        return instancePlugins.stream()
            .map(InstancePlugin::getSourceTechnology)
            .collect(Collectors.toSet());
    }

    public Optional<InstancePlugin<?>> findBySourceTechnology(SourceTechnology sourceTechnology) {
        if (sourceTechnology == null) {
            return Optional.empty();
        }
        for (InstancePlugin<?> instancePlugin : instancePlugins) {
            if (instancePlugin.getSourceTechnology().getId().equals(sourceTechnology.getId())) {
                return Optional.of(instancePlugin);
            }
        }
        return Optional.empty();
    }
}
