package io.github.edmm.core.plugin;

import java.util.List;

import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public abstract class InstancePlugin<L extends AbstractLifecycleInstancePlugin> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SourceTechnology sourceTechnology;

    public InstancePlugin(@NonNull SourceTechnology sourceTechnology) {
        this.sourceTechnology = sourceTechnology;
        logger.debug("Initializing plugin '{}'", sourceTechnology.getName());
        this.init();
        logger.debug("Initialized plugin '{}'", sourceTechnology.getName());
    }

    private void init() {
        // noop
    }

    public void execute(InstanceTransformationContext context) throws Exception {
        long time = System.currentTimeMillis();
        L lifecycle = getLifecycle(context);
        List<InstanceLifecyclePhase> phases = lifecycle.getInstanceLifecyclePhases();
        int taskCount = countExecutionPhases(context, phases);
        logger.debug("This transformation has {} phases", taskCount);
        for (int i = 0; i < phases.size(); i++) {
            @SuppressWarnings("unchecked")
            InstanceLifecyclePhase<L> phase = (InstanceLifecyclePhase<L>) phases.get(i);
            if (phase.shouldExecute(context)) {
                logger.debug("Executing phase '{}' ({} of {})", phase.getName(), (i + 1), taskCount);
                phase.execute(lifecycle);
            } else {
                phase.skip();
                logger.debug("Skipping phase '{}' ({} of {})", phase.getName(), (i + 1), taskCount);
            }
        }
        time = System.currentTimeMillis() - time;
        logger.info("Transformation finished after {} ms", time);
    }

    private int countExecutionPhases(InstanceTransformationContext context, List<? extends InstanceLifecyclePhase> phases) {
        return (int) phases.stream().filter(e -> e.shouldExecute(context)).count();
    }

    public abstract L getLifecycle(InstanceTransformationContext context);
}
