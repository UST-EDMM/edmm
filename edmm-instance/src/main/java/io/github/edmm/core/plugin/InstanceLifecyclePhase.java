package io.github.edmm.core.plugin;

import java.util.function.Predicate;

import io.github.edmm.core.plugin.support.ExecutionFunction;
import io.github.edmm.core.transformation.InstanceTransformationContext;

import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class InstanceLifecyclePhase<L extends InstancePluginLifecycle> {

    private static final Logger logger = LoggerFactory.getLogger(InstanceLifecyclePhase.class);

    private final Phases phase;
    private final AbstractLifecycleInstancePlugin<L> phaseAccess;
    private final ExecutionFunction<L> function;

    private State state = State.PENDING;
    private final Predicate<InstanceTransformationContext> predicate = (c) -> true;

    InstanceLifecyclePhase(@NonNull Phases phase, @NonNull AbstractLifecycleInstancePlugin<L> phaseAccess, @NonNull ExecutionFunction<L> function) {
        this.phase = phase;
        this.phaseAccess = phaseAccess;
        this.function = function;
    }

    private void setState(State state) {
        if (this.state == state) return;
        this.state = state;
        logger.info(String.format("%-25s  %-10s", "Phase '" + this.phase + "'", this.state));
    }

    void skip() {
        setState(State.SKIPPED);
    }

    boolean shouldExecute(InstanceTransformationContext context) {
        boolean shouldExecute = predicate.test(context);
        if (!shouldExecute && getState() == State.PENDING) {
            setState(State.SKIPPING);
        }
        return shouldExecute;
    }

    void execute(L lifecycle) throws Exception {
        try {
            setState(State.EXECUTING);
            function.apply(lifecycle);
            setState(State.DONE);
        } catch (Exception e) {
            setState(State.FAILED);
            throw e;
        }
    }

    public enum State {
        PENDING,
        SKIPPING,
        EXECUTING,
        DONE,
        SKIPPED,
        FAILED,
    }
}
