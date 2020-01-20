package io.github.edmm.core.transformation.support;

import java.io.File;
import java.util.concurrent.Callable;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.core.transformation.TransformationContext.State.DONE;
import static io.github.edmm.core.transformation.TransformationContext.State.ERROR;
import static io.github.edmm.core.transformation.TransformationContext.State.TRANSFORMING;

public final class ExecutionTask implements Callable<Void> {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionTask.class);

    private final Plugin plugin;
    private final TransformationContext context;

    public ExecutionTask(@NonNull Plugin plugin, @NonNull TransformationContext context) {
        this.plugin = plugin;
        this.context = context;
    }

    @Override
    public Void call() {
        boolean failed = false;
        TargetTechnology targetTechnology = plugin.getTargetTechnology();
        logger.info("Starting transformation for {}", targetTechnology.getName());
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
            context.setState(DONE);
        } catch (Exception e) {
            logger.info("Transformation to {} failed", targetTechnology.getName());
            logger.error("Something went wrong while transforming", e);
            failed = true;
        }
        context.setState(failed ? ERROR : DONE);
        return null;
    }
}
