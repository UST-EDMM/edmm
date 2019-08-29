package io.github.edmm.core.transformation.support;

import java.io.File;
import java.util.concurrent.Callable;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.Transformation;
import io.github.edmm.core.transformation.TransformationContext;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExecutionTask implements Callable<Void> {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionTask.class);

    private final Plugin plugin;
    private final Transformation transformation;
    private final File sourceDirectory;
    private final File targetDirectory;

    private boolean failed = false;

    public ExecutionTask(@NonNull Plugin plugin, @NonNull Transformation transformation,
                         @NonNull File sourceDirectory, @NonNull File targetDirectory) {
        this.plugin = plugin;
        this.transformation = transformation;
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
    }

    @Override
    public Void call() {
        String templateName = transformation.getModel().getName();
        String platformId = plugin.getPlatform().getId();
        logger.info("Starting transformation executor for {}/{}", templateName, platformId);
        transformation.setState(Transformation.State.TRANSFORMING);
        if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
            logger.error("Could not create directory at '{}'", targetDirectory.getAbsolutePath());
            transformation.setState(Transformation.State.ERROR);
            return null;
        }
        if (!targetDirectory.isDirectory() || !targetDirectory.canWrite()) {
            logger.error("Given value is not a directory or not writable: {}", targetDirectory.getAbsolutePath());
            transformation.setState(Transformation.State.ERROR);
            return null;
        }
        try {
            plugin.transform(new TransformationContext(transformation, sourceDirectory, targetDirectory));
            transformation.setState(Transformation.State.DONE);
        } catch (Exception e) {
            logger.info("Transformation of {}/{} failed", templateName, platformId);
            logger.error("Something went wrong while transforming", e);
            failed = true;
        }
        transformation.setState(failed ? Transformation.State.ERROR : Transformation.State.DONE);
        return null;
    }
}
