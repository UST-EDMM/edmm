package io.github.edmm.plugins;

import java.io.File;

import io.github.edmm.core.execution.ExecutionContext;
import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.ExecutionPlugin;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TransformationContext;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.core.plugin.support.CheckModelResult.State.OK;
import static io.github.edmm.core.transformation.TransformationContext.State.DONE;
import static io.github.edmm.core.transformation.TransformationContext.State.TRANSFORMING;

public abstract class PluginTest {

    private static final Logger logger = LoggerFactory.getLogger(PluginTest.class);

    protected final File targetDirectory;

    public PluginTest(File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    protected void executeLifecycle(TransformationPlugin<?> plugin, TransformationContext context) {
        AbstractLifecycle lifecycle = plugin.getLifecycle(context);
        CheckModelResult result = lifecycle.checkModel();
        logger.info("checkModel(): state={}, unsupportedComponents={}",
            result.getState(), result.getUnsupportedComponents());
        if (OK.equals(result.getState())) {
            context.setState(TRANSFORMING);
            lifecycle.prepare();
            lifecycle.transform();
            lifecycle.cleanup();
            context.setState(DONE);
        } else {
            logger.warn("Skip execution due to unsupported components...");
        }
        plugin.finalize(context);
    }

    protected void executeDeployment(ExecutionPlugin plugin, ExecutionContext context) {
        plugin.init();
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

    @After
    public void destroy() throws Exception {
        if (targetDirectory != null) {
            logger.info("Clean up working directory...");
            FileUtils.deleteDirectory(targetDirectory);
        }
    }
}
