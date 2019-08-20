package io.github.edmm.core.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.github.edmm.core.transformation.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginService {

    private static final Logger logger = LoggerFactory.getLogger(PluginService.class);

    private final List<Plugin> plugins;
    private final Set<Platform> platforms = new HashSet<>();

    public PluginService(List<Plugin> plugins) {
        this.plugins = plugins;
        Map<String, Plugin> pluginMap = new HashMap<>();
        for (Plugin plugin : plugins) {
            if (pluginMap.get(plugin.getPlatform().id) != null) {
                logger.error("Found duplicate plugin identifier '{}'", plugin.getPlatform().id);
                throw new IllegalArgumentException("The platform id '" + plugin.getPlatform().id + "' is not unique");
            }
            pluginMap.put(plugin.getPlatform().id, plugin);
        }
        for (Plugin plugin : plugins) {
            platforms.add(plugin.getPlatform());
        }
        logger.info("Loaded {} plugins", plugins.size());
    }

    public List<Plugin> getPlugins() {
        return new ArrayList<>(plugins);
    }

    public Set<Platform> getSupportedPlatforms() {
        return new HashSet<>(platforms);
    }

    public boolean isSupported(Platform platform) {
        return platforms.contains(platform);
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
}
