package io.github.edmm.core.plugin;

import java.util.function.Predicate;

import io.github.edmm.core.plugin.support.ExecutionFunction;
import io.github.edmm.core.plugin.support.LifecyclePhaseAccess;
import io.github.edmm.core.transformation.TransformationContext;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class LifecyclePhase<L extends PluginLifecycle> {

    private static final Logger logger = LoggerFactory.getLogger(LifecyclePhase.class);

    private final String name;
    private final LifecyclePhaseAccess phaseAccess;
    private final ExecutionFunction<L> function;

    private State state = State.PENDING;
    private Predicate<TransformationContext> predicate = (c) -> true;

    public LifecyclePhase(@NonNull String name, @NonNull LifecyclePhaseAccess phaseAccess, @NonNull ExecutionFunction<L> function) {
        this.name = name;
        this.phaseAccess = phaseAccess;
        this.function = function;
    }

    public void setState(State state) {
        if (this.state == state) return;
        logger.debug(String.format("%-20s  %-10s ==> %s", "Phase '" + this.name + "':", this.state, state));
        this.state = state;
        if (state == State.FAILED) {
            skipSuccessors();
        }
    }

    public void skip() {
        setState(State.SKIPPED);
    }

    public boolean shouldExecute(TransformationContext context) {
        boolean shouldExecute = predicate.test(context);
        if (!shouldExecute && getState() == State.PENDING) {
            setState(State.SKIPPING);
        }
        return shouldExecute;
    }

    public void execute(L lifecycle) throws Exception {
        try {
            setState(State.EXECUTING);
            function.apply(lifecycle);
            setState(State.DONE);
        } catch (Exception e) {
            setState(State.FAILED);
            throw e;
        }
    }

    private void skipSuccessors() {
        boolean skip = true;
        for (LifecyclePhase phase : phaseAccess.getLifecyclePhases()) {
            if (phase == this) {
                skip = false;
            }
            if (!skip) {
                skip();
            }
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
