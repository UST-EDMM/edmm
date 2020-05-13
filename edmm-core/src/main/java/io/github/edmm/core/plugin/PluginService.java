package io.github.edmm.core.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.PluginSupportResult;
import io.github.edmm.plugins.rules.Rule;
import io.github.edmm.plugins.rules.RuleEngine;

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

    public Set<DeploymentTechnology> getSupportedTransformationTargets() {
        return transformationPlugins.stream()
            .map(TransformationPlugin::getDeploymentTechnology)
            .collect(Collectors.toSet());
    }

    public Set<DeploymentTechnology> getSupportedExecutionTargets() {
        return executionPlugins.stream()
            .map(ExecutionPlugin::getDeploymentTechnology)
            .collect(Collectors.toSet());
    }

    public List<TransformationPlugin<?>> getTransformationPlugins() {
        return Collections.unmodifiableList(transformationPlugins);
    }

    public List<ExecutionPlugin> getExecutionPlugins() {
        return Collections.unmodifiableList(executionPlugins);
    }

    public Optional<TransformationPlugin<?>> getTransformationPlugin(DeploymentTechnology dt) {
        if (dt == null) {
            return Optional.empty();
        }
        for (TransformationPlugin<?> plugin : transformationPlugins) {
            if (plugin.getDeploymentTechnology().getId().equals(dt.getId())) {
                return Optional.of(plugin);
            }
        }
        return Optional.empty();
    }

    public Optional<ExecutionPlugin> getExecutionPlugin(DeploymentTechnology dt) {
        if (dt == null) {
            return Optional.empty();
        }
        for (ExecutionPlugin plugin : executionPlugins) {
            if (plugin.getDeploymentTechnology().getId().equals(dt.getId())) {
                return Optional.of(plugin);
            }
        }
        return Optional.empty();
    }

    public List<PluginSupportResult> checkModelSupport(DeploymentModel model) {
        List<PluginSupportResult> response = new ArrayList<>();
        for (TransformationPlugin<?> plugin : this.transformationPlugins) {
            TransformationContext context = new TransformationContext(model, plugin.getDeploymentTechnology());

            RuleEngine ruleEngine = new RuleEngine();
            ruleEngine.fire(context,plugin);
            Map<String,List<Rule.Result>> ruleResults = ruleEngine.getResults();

            PluginSupportResult.PluginSupportResultBuilder psr = PluginSupportResult.builder()
                .id(plugin.getDeploymentTechnology().getId())
                .name(plugin.getDeploymentTechnology().getName())
                .replacementRules(ruleResults);
            double s = 1 - (ruleResults.keySet().size() / (double) model.getComponents().size());
            psr.supports(s);
            response.add(psr.build());
        }

        return response;
    }
}
