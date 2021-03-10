package io.github.edmm.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.execution.ExecutionContext;
import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.ExecutionPlugin;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.parameters.ParameterInstance;
import io.github.edmm.plugins.rules.RuleEngine;
import io.github.edmm.utils.Env;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.model.parameters.ParameterInstance.isValid;

public abstract class PluginTest {

    private static final Logger logger = LoggerFactory.getLogger(PluginTest.class);

    private boolean skipCleanup = false;
    protected File repositoryDirectory;
    protected File inputFile;
    protected File targetDirectory;

    public PluginTest(File repositoryDirectory, File inputFile, File targetDirectory) throws Exception {
        this.repositoryDirectory = repositoryDirectory;
        this.inputFile = inputFile;
        this.targetDirectory = targetDirectory;
        String repositoryValue = Env.get("REPOSITORY", null);
        String inputFileValue = Env.get("INPUT_FILE", null);
        String outputDirectoryValue = Env.get("OUTPUT_DIR", null);
        if (repositoryValue != null && inputFileValue != null && outputDirectoryValue != null) {
            this.skipCleanup = true;
            this.repositoryDirectory = new File(repositoryValue);
            this.inputFile = new File(inputFileValue);
            this.targetDirectory = new File(outputDirectoryValue);
        }
        logger.info("Source directory is '{}'", this.repositoryDirectory.getCanonicalPath());
        logger.info("Target directory is '{}'", this.targetDirectory.getCanonicalPath());
        logger.info("Input file at '{}'", this.inputFile.getCanonicalPath());
    }

    protected void executeLifecycle(TransformationPlugin<?> plugin) {
        executeLifecycle(plugin, new TransformationContext(inputFile, DeploymentTechnology.NOOP, repositoryDirectory, targetDirectory));
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
            long unsupportedRulesCount = RuleEngine.countUnsupportedRules(ruleEngine.fire(context, plugin));

            logger.info("RuleEngine.fire(): unsupportedRuless={}", unsupportedRulesCount);

            if (unsupportedRulesCount == 0) {
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

    protected void executeDeployment(ExecutionPlugin plugin, ExecutionContext context) throws Exception {
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
                plugin.finalize(context);
                throw e;
            }
            plugin.finalize(context);
            context.setState(ExecutionContext.State.DONE);
        }
    }

    @After
    public void destroy() throws Exception {
        if (targetDirectory != null && !skipCleanup) {
            logger.info("Clean up working directory...");
            FileUtils.deleteDirectory(targetDirectory);
        }
    }
}
