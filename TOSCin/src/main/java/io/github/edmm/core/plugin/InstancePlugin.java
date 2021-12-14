package io.github.edmm.core.plugin;

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
        logger.debug("Initialized instance transformation plugin '{}'", sourceTechnology.getName());
    }

    public void execute() throws Exception {
        this.execute(this.lifecycle);
    }

    private void execute(L lifecycle) throws Exception {
        long time = System.currentTimeMillis();
        for (InstanceLifecyclePhase<L> phase : lifecycle.getPhases()) {
            if (phase.shouldExecute(lifecycle.context)) {
                phase.execute(lifecycle);
            } else {
                logger.info("Skipping phase |{}|", phase.getPhase().name());
                phase.skip();
            }
        }
        time = System.currentTimeMillis() - time;
        logger.info("EDMMi Transformation finished after {} ms", time);
    }
}
