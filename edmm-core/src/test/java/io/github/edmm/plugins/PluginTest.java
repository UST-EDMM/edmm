package io.github.edmm.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.edmm.core.execution.ExecutionContext;
import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.ExecutionPlugin;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.parameters.ParameterInstance;

import io.github.edmm.plugins.rules.Rule;
import io.github.edmm.plugins.rules.RuleEngine;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.core.plugin.support.CheckModelResult.State.OK;
import static io.github.edmm.model.parameters.ParameterInstance.isValid;

public abstract class PluginTest {

    private static final Logger logger = LoggerFactory.getLogger(PluginTest.class);

    protected final File targetDirectory;

    public PluginTest(File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    protected void executeLifecycle(TransformationPlugin<?> plugin, TransformationContext context) {
        List<String> errors = new ArrayList<>();
        ParameterInstance.of(context.getUserInputs(), plugin.getDeploymentTechnology().getExecutionParameters()).forEach(p -> {
            if (!isValid(p)) {
                errors.add(String.format("Parameter '%s' is not valid; must be a valid %s value, but is '%s'", p.getName(), p.getType().getName(), p.getValue()));
            }
        });
        if (errors.size() > 0) {
            context.setState(TransformationContext.State.ERROR);
            context.putValue("errors", errors);
        }
        if (context.getState() == TransformationContext.State.READY) {
            AbstractLifecycle lifecycle = plugin.getLifecycle(context);

            RuleEngine ruleEngine = new RuleEngine();
            ruleEngine.fire(context,plugin);
            Set<String> unsupportedComponents = ruleEngine.getResults().keySet();

            logger.info("RuleEngine.fire(): unsupportedComponents={}", unsupportedComponents);

            if (unsupportedComponents.isEmpty()) {
                context.setState(TransformationContext.State.TRANSFORMING);
                lifecycle.prepare();
                lifecycle.transform();
                lifecycle.cleanup();
                context.setState(TransformationContext.State.DONE);
            } else {
                logger.warn("Skip execution due to unsupported components...");
            }
            plugin.finalize(context);
        }
    }

    protected void executeDeployment(ExecutionPlugin plugin, ExecutionContext context) {
        plugin.init();
        List<String> errors = new ArrayList<>();
        ParameterInstance.of(context.getUserInputs(), plugin.getDeploymentTechnology().getExecutionParameters()).forEach(p -> {
            if (!isValid(p)) {
                errors.add(String.format("Parameter '%s' is not valid; must be a valid %s value, but is '%s'", p.getName(), p.getType().getName(), p.getValue()));
            }
        });
        if (errors.size() > 0) {
            context.setState(ExecutionContext.State.ERROR);
            context.putValue("errors", errors);
        }
        if (context.getState() == ExecutionContext.State.READY) {
            try {
                context.setState(ExecutionContext.State.DEPLOYING);
                plugin.execute(context);
            } catch (Exception e) {
                logger.error("Error executing deployment: {}", e.getMessage(), e);
                context.setState(ExecutionContext.State.ERROR);
            }
            plugin.finalize(context);
            context.setState(ExecutionContext.State.DONE);
        }
    }

    @After
    public void destroy() throws Exception {
        if (targetDirectory != null) {
            logger.info("Clean up working directory...");
            FileUtils.deleteDirectory(targetDirectory);
        }
    }
}
