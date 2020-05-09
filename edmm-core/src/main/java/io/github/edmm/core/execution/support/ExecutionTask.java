package io.github.edmm.core.execution.support;

import java.io.File;
import java.util.concurrent.Callable;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.execution.ExecutionContext;
import io.github.edmm.core.plugin.ExecutionPlugin;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.core.execution.ExecutionContext.State.DEPLOYING;
import static io.github.edmm.core.execution.ExecutionContext.State.DONE;
import static io.github.edmm.core.execution.ExecutionContext.State.ERROR;

public final class ExecutionTask implements Callable<Void> {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionTask.class);

    private final ExecutionPlugin plugin;
    private final ExecutionContext context;

    public ExecutionTask(@NonNull ExecutionPlugin plugin, @NonNull ExecutionContext context) {
        this.plugin = plugin;
        this.context = context;
    }

    @Override
    public Void call() {
        DeploymentTechnology technology = context.getTransformation().getDeploymentTechnology();
        logger.info("Starting deployment for {}", technology.getName());
        context.setState(DEPLOYING);
        File directory = context.getDirectory();
        if (!directory.exists()) {
            logger.error("Given directory does not exist at '{}'", directory.getAbsolutePath());
            context.setState(ERROR);
            return null;
        }
        if (!directory.isDirectory() || !directory.canWrite()) {
            logger.error("Given directory is not writable: {}", directory.getAbsolutePath());
            context.setState(ERROR);
            return null;
        }
        try {
            plugin.execute(context);
            plugin.finalize(context);
            context.setState(DONE);
        } catch (Exception e) {
            logger.info("Deployment with {} failed", technology.getName());
            logger.error("Something went wrong while deployment", e);
            context.setState(ERROR);
        }
        return null;
    }
}
