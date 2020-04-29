package io.github.edmm.core.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.PluginSupportResult;
import io.github.edmm.model.component.RootComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PluginService {

    private static final Logger logger = LoggerFactory.getLogger(PluginService.class);

    private final List<Plugin<?>> plugins;

    @Autowired
    public PluginService(List<Plugin<?>> plugins) {
        this.plugins = plugins;
        Map<String, Plugin<?>> pluginMap = new HashMap<>();
        for (Plugin<?> plugin : plugins) {
            if (pluginMap.get(plugin.getTargetTechnology().getId()) != null) {
                logger.error("Found duplicate plugin identifier '{}'", plugin.getTargetTechnology().getId());
                throw new IllegalArgumentException("The id '" + plugin.getTargetTechnology().getId() + "' is not unique");
            }
            pluginMap.put(plugin.getTargetTechnology().getId(), plugin);
        }
        logger.debug("Loaded {} plugins", plugins.size());
    }

    public List<Plugin<?>> getPlugins() {
        return new ArrayList<>(plugins);
    }

    public Set<TargetTechnology> getSupportedTargetTechnologies() {
        return plugins.stream()
            .map(Plugin::getTargetTechnology)
            .collect(Collectors.toSet());
    }

    public Optional<Plugin<?>> findByTargetTechnology(TargetTechnology targetTechnology) {
        if (targetTechnology == null) {
            return Optional.empty();
        }
        for (Plugin<?> plugin : plugins) {
            if (plugin.getTargetTechnology().getId().equals(targetTechnology.getId())) {
                return Optional.of(plugin);
            }
        }
        return Optional.empty();
    }

    public CheckModelResult checkModel(TransformationContext context, Plugin<?> plugin) {
        return plugin.getLifecycle(context).checkModel();
    }

    public List<PluginSupportResult> checkModelSupport(DeploymentModel model) {
        List<PluginSupportResult> response = new ArrayList<>();
        for (Plugin<?> plugin : this.plugins) {
            TransformationContext context = new TransformationContext(model, plugin.getTargetTechnology());
            CheckModelResult checkModelResult = this.checkModel(context, plugin);
            List<String> unsupportedComponents = checkModelResult.getUnsupportedComponents().stream()
                .map(RootComponent::getName)
                .collect(Collectors.toList());
            PluginSupportResult.PluginSupportResultBuilder psr = PluginSupportResult.builder()
                .id(plugin.getTargetTechnology().getId())
                .name(plugin.getTargetTechnology().getName())
                .unsupportedComponents(unsupportedComponents);
            double s = 1 - (unsupportedComponents.size() / (double) model.getComponents().size());
            psr.supports(s);
            response.add(psr.build());
        }

        return response;
    }
}
