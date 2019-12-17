package io.github.edmm.core.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.Platform;
import io.github.edmm.core.transformation.TransformationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PluginService {

    private static final Logger logger = LoggerFactory.getLogger(PluginService.class);

    private final List<Plugin> plugins;

    @Autowired
    public PluginService(List<Plugin> plugins) {
        this.plugins = plugins;
        Map<String, Plugin> pluginMap = new HashMap<>();
        for (Plugin plugin : plugins) {
            if (pluginMap.get(plugin.getPlatform().getId()) != null) {
                logger.error("Found duplicate plugin identifier '{}'", plugin.getPlatform().getId());
                throw new IllegalArgumentException("The platform id '" + plugin.getPlatform().getId() + "' is not unique");
            }
            pluginMap.put(plugin.getPlatform().getId(), plugin);
        }

        logger.debug("Loaded {} plugins", plugins.size());
    }

    public List<Plugin> getPlugins() {
        return new ArrayList<>(plugins);
    }

    public Set<Platform> getSupportedPlatforms() {
        return plugins.stream()
                .map(Plugin::getPlatform)
                .collect(Collectors.toSet());
    }

    public Optional<Plugin> findByPlatform(Platform platform) {
        if (platform == null) {
            return Optional.empty();
        }
        for (Plugin plugin : plugins) {
            if (plugin.getPlatform().getId().equals(platform.getId())) {
                return Optional.of(plugin);
            }
        }
        return Optional.empty();
    }

    public CheckModelResult checkModel(TransformationContext context, Plugin plugin) {
        return plugin.getLifecycle(context).checkModel();
    }
}
