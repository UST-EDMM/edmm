package io.github.edmm.core.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.edmm.core.TargetTechnology;
import io.github.edmm.core.plugin.support.CheckModelResult;
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

    private final List<TransformationPlugin<?>> transformationPlugins;
    private final List<ExecutionPlugin> executionPlugins;

    @Autowired
    public PluginService(List<TransformationPlugin<?>> transformationPlugins, List<ExecutionPlugin> executionPlugins) {
        this.transformationPlugins = transformationPlugins;
        this.executionPlugins = executionPlugins;
        logger.debug("Loaded {} transformation plugin(s)", transformationPlugins.size());
        logger.debug("Loaded {} execution plugin(s)", executionPlugins.size());
    }

    public Set<TargetTechnology> getSupportedTransformationTargets() {
        return transformationPlugins.stream()
            .map(TransformationPlugin::getTargetTechnology)
            .collect(Collectors.toSet());
    }

    public Set<TargetTechnology> getSupportedExecutionTargets() {
        return executionPlugins.stream()
            .map(ExecutionPlugin::getTargetTechnology)
            .collect(Collectors.toSet());
    }

    public List<TransformationPlugin<?>> getTransformationPlugins() {
        return Collections.unmodifiableList(transformationPlugins);
    }

    public List<ExecutionPlugin> getExecutionPlugins() {
        return Collections.unmodifiableList(executionPlugins);
    }

    public Optional<TransformationPlugin<?>> getTransformationPlugin(TargetTechnology technology) {
        if (technology == null) {
            return Optional.empty();
        }
        for (TransformationPlugin<?> plugin : transformationPlugins) {
            if (plugin.getTargetTechnology().getId().equals(technology.getId())) {
                return Optional.of(plugin);
            }
        }
        return Optional.empty();
    }

    public Optional<ExecutionPlugin> getExecutionPlugin(TargetTechnology technology) {
        if (technology == null) {
            return Optional.empty();
        }
        for (ExecutionPlugin plugin : executionPlugins) {
            if (plugin.getTargetTechnology().getId().equals(technology.getId())) {
                return Optional.of(plugin);
            }
        }
        return Optional.empty();
    }

    public CheckModelResult checkModel(TransformationContext context, TransformationPlugin<?> plugin) {
        return plugin.getLifecycle(context).checkModel();
    }

    public List<PluginSupportResult> checkModelSupport(DeploymentModel model) {
        List<PluginSupportResult> response = new ArrayList<>();
        for (TransformationPlugin<?> plugin : this.transformationPlugins) {
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
