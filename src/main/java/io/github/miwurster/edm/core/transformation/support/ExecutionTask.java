package io.github.miwurster.edm.core.transformation.support;

import java.io.File;
import java.util.concurrent.Callable;

import io.github.miwurster.edm.core.plugin.Plugin;
import io.github.miwurster.edm.core.transformation.Transformation;
import io.github.miwurster.edm.core.transformation.TransformationContext;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExecutionTask implements Callable<Void> {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionTask.class);

    private final Plugin plugin;
    private final Transformation transformation;
    private final File rootDirectory;

    private boolean failed = false;

    public ExecutionTask(@NonNull Plugin plugin, @NonNull Transformation transformation, @NonNull File rootDirectory) {
        this.plugin = plugin;
        this.transformation = transformation;
        this.rootDirectory = rootDirectory;
    }

    @Override
    public Void call() {
        String templateName = transformation.getModel().getName();
        String platformId = plugin.getPlatform().getId();
        logger.info("Starting transformation executor for {}/{}", templateName, platformId);
        transformation.setState(Transformation.State.TRANSFORMING);
        if (!rootDirectory.exists() && !rootDirectory.mkdirs()) {
            logger.error("Could not create directory at '{}'", rootDirectory.getAbsolutePath());
            transformation.setState(Transformation.State.ERROR);
            return null;
        }
        if (!rootDirectory.isDirectory() || !rootDirectory.canWrite()) {
            logger.error("Given value is not a directory or not writable: {}", rootDirectory.getAbsolutePath());
            transformation.setState(Transformation.State.ERROR);
            return null;
        }
        try {
            plugin.transform(new TransformationContext(transformation, rootDirectory));
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
