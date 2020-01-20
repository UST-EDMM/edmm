package io.github.edmm.core.plugin;

import java.util.List;

import io.github.edmm.core.transformation.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public abstract class Plugin<L extends AbstractLifecycle> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TargetTechnology targetTechnology;

    public Plugin(@NonNull TargetTechnology targetTechnology) {
        this.targetTechnology = targetTechnology;
        logger.debug("Initializing plugin '{}'", targetTechnology.getName());
        this.init();
        logger.debug("Initialized plugin '{}'", targetTechnology.getName());
    }

    protected void init() {
        // noop
    }

    public void execute(TransformationContext context) throws Exception {
        long time = System.currentTimeMillis();
        L lifecycle = getLifecycle(context);
        List<LifecyclePhase> phases = lifecycle.getLifecyclePhases();
        int taskCount = countExecutionPhases(context, phases);
        logger.debug("This transformation has {} phases", taskCount);
        for (int i = 0; i < phases.size(); i++) {
            @SuppressWarnings("unchecked")
            LifecyclePhase<L> phase = (LifecyclePhase<L>) phases.get(i);
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

    private int countExecutionPhases(TransformationContext context, List<? extends LifecyclePhase> phases) {
        return (int) phases.stream().filter(e -> e.shouldExecute(context)).count();
    }

    public abstract L getLifecycle(TransformationContext context);
}
