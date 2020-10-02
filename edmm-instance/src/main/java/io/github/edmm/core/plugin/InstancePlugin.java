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

    protected InstancePlugin(@NonNull SourceTechnology sourceTechnology) {
        this.sourceTechnology = sourceTechnology;
        logger.debug("Initializing instance transformation plugin '{}'", sourceTechnology.getName());
        this.init();
        logger.debug("Initialized instance transformation plugin '{}'", sourceTechnology.getName());
    }

    private void init() {
        // noop
    }

    public void execute(InstanceTransformationContext context) throws Exception {
        long time = System.currentTimeMillis();
        L lifecycle = getLifecycle(context);
        List<InstanceLifecyclePhase> phases = lifecycle.getInstanceLifecyclePhases();
        for (InstanceLifecyclePhase phase1 : phases) {
            @SuppressWarnings("unchecked")
            InstanceLifecyclePhase<L> phase = (InstanceLifecyclePhase<L>) phase1;
            if (phase.shouldExecute(context)) {
                phase.execute(lifecycle);
            } else {
                phase.skip();
            }
        }
        time = System.currentTimeMillis() - time;
        logger.info("EDMMi Transformation finished after {} ms", time);
    }

    private int countExecutionPhases(InstanceTransformationContext context, List<? extends InstanceLifecyclePhase> phases) {
        return (int) phases.stream().filter(e -> e.shouldExecute(context)).count();
    }

    public abstract L getLifecycle(InstanceTransformationContext context);
}
