package io.github.edmm.core.plugin;

import java.util.List;

import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;

import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class InstancePlugin<L extends AbstractLifecycleInstancePlugin<L>> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SourceTechnology sourceTechnology;
    private final L lifecycle;

    protected InstancePlugin(@NonNull SourceTechnology sourceTechnology) {
        this(sourceTechnology, null);
    }

    public InstancePlugin(@NonNull SourceTechnology sourceTechnology, L lifecycle) {
        this.lifecycle = lifecycle;
        this.sourceTechnology = sourceTechnology;
        logger.debug("Initializing instance transformation plugin '{}'", sourceTechnology.getName());
        this.init();
        logger.debug("Initialized instance transformation plugin '{}'", sourceTechnology.getName());
    }

    private void init() {
        // noop
    }

    public void execute() throws Exception {
        this.execute(this.lifecycle);
    }

    public void execute(InstanceTransformationContext context) throws Exception {
        this.execute(getLifecycle(context));
    }

    private void execute(L lifecycle) throws Exception {
        long time = System.currentTimeMillis();
        for (InstanceLifecyclePhase<L> phase : lifecycle.getPhases()) {
            if (phase.shouldExecute(lifecycle.context)) {
                phase.execute(lifecycle);
            } else {
                phase.skip();
            }
        }
        time = System.currentTimeMillis() - time;
        logger.info("EDMMi Transformation finished after {} ms", time);
    }

    private int countExecutionPhases(InstanceTransformationContext context, List<? extends InstanceLifecyclePhase<?>> phases) {
        return (int) phases.stream().filter(e -> e.shouldExecute(context)).count();
    }

    public L getLifecycle(InstanceTransformationContext context) {
        return this.lifecycle;
    }
}
