package io.github.edmm.core.transformation.support;

import java.io.File;
import java.util.concurrent.Callable;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.TransformationContext;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.core.transformation.TransformationContext.State.DONE;
import static io.github.edmm.core.transformation.TransformationContext.State.ERROR;
import static io.github.edmm.core.transformation.TransformationContext.State.TRANSFORMING;

public final class TransformationTask implements Callable<Void> {

    private static final Logger logger = LoggerFactory.getLogger(TransformationTask.class);

    private final TransformationPlugin<?> plugin;
    private final TransformationContext context;

    public TransformationTask(@NonNull TransformationPlugin<?> plugin, @NonNull TransformationContext context) {
        this.plugin = plugin;
        this.context = context;
    }

    @Override
    public Void call() {
        DeploymentTechnology deploymentTechnology = plugin.getDeploymentTechnology();
        logger.info("Starting transformation for {}", deploymentTechnology.getName());
        context.setState(TRANSFORMING);
        File targetDirectory = context.getTargetDirectory();
        if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
            logger.error("Could not create directory at '{}'", targetDirectory.getAbsolutePath());
            context.setState(ERROR);
            return null;
        }
        if (!targetDirectory.isDirectory() || !targetDirectory.canWrite()) {
            logger.error("Given value is not a directory or not writable: {}", targetDirectory.getAbsolutePath());
            context.setState(ERROR);
            return null;
        }
        try {
            plugin.execute(context);
            plugin.finalize(context);
            context.setState(DONE);
        } catch (Exception e) {
            logger.info("Transformation to {} failed", deploymentTechnology.getName());
            logger.error("Something went wrong while transforming", e);
            context.setState(ERROR);
        }
        return null;
    }
}
