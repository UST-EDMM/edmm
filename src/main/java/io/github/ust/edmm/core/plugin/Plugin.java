package io.github.ust.edmm.core.plugin;

import java.util.List;

import io.github.ust.edmm.core.transformation.Platform;
import io.github.ust.edmm.core.transformation.TransformationContext;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public abstract class Plugin<LifecycleT extends AbstractLifecycle> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Platform platform;

    public Plugin(@NonNull Platform platform) {
        this.platform = platform;
        logger.info("Initializing plugin '{}'", this.platform.name);
        this.init();
        logger.info("Initialized plugin '{}'", this.platform.name);
    }

    protected void init() {
        // noop
    }

    public void transform(TransformationContext context) throws Exception {
        long time = System.currentTimeMillis();
        LifecycleT lifecycle = getLifecycle(context);
        List<LifecyclePhase> phases = lifecycle.getLifecyclePhases();
        int taskCount = countExecutionPhases(context, phases);
        logger.info("This transformation has {} phases", taskCount);
        for (int i = 0; i < phases.size(); i++) {
            @SuppressWarnings("unchecked")
            LifecyclePhase<LifecycleT> phase = (LifecyclePhase<LifecycleT>) phases.get(i);
            if (phase.shouldExecute(context)) {
                logger.info("Executing phase '{}' ({} of {})", phase.getName(), (i + 1), taskCount);
                phase.execute(lifecycle);
            } else {
                phase.skip();
                logger.info("Skipping phase '{}' ({} of {})", phase.getName(), (i + 1), taskCount);
            }
        }
        time = System.currentTimeMillis() - time;
        logger.info("Transformation finished after {} ms", time);
    }

    private int countExecutionPhases(TransformationContext context, List<? extends LifecyclePhase> phases) {
        return (int) phases.stream().filter(e -> e.shouldExecute(context)).count();
    }

    public abstract LifecycleT getLifecycle(TransformationContext context);
}
