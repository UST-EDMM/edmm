package io.github.edmm.core.transformation.support;

import java.util.concurrent.Callable;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.core.transformation.InstanceTransformationContext.State.DONE;
import static io.github.edmm.core.transformation.InstanceTransformationContext.State.ERROR;
import static io.github.edmm.core.transformation.InstanceTransformationContext.State.TRANSFORMING;

public class InstanceExecutionTask implements Callable<Void> {
    private static final Logger logger = LoggerFactory.getLogger(ExecutionTask.class);

    private final InstancePlugin<?> plugin;
    private final InstanceTransformationContext context;

    public InstanceExecutionTask(@NonNull InstancePlugin<?> plugin, @NonNull InstanceTransformationContext context) {
        this.plugin = plugin;
        this.context = context;
    }

    @Override
    public Void call() {
        boolean failed = false;
        SourceTechnology sourceTechnology = plugin.getSourceTechnology();
        logger.info("Starting transformation from {}", sourceTechnology.getName());
        context.setState(TRANSFORMING);
        try {
            plugin.execute(context);
            context.setState(DONE);
        } catch (Exception e) {
            logger.info("Transformation from {} failed", sourceTechnology.getName());
            logger.error("Something went wrong while transforming", e);
            failed = true;
        }
        context.setState(failed ? ERROR : DONE);
        return null;
    }
}
