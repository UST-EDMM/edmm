package io.github.edmm.core.plugin;

import io.github.edmm.core.plugin.support.ExecutionFunction;
import io.github.edmm.core.plugin.support.InstanceLifecyclePhaseAccess;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

@Getter
public class InstanceLifecyclePhase<L extends InstancePluginLifecycle> {

    private static final Logger logger = LoggerFactory.getLogger(InstanceLifecyclePhase.class);

    private final String name;
    private final InstanceLifecyclePhaseAccess phaseAccess;
    private final ExecutionFunction<L> function;

    private State state = State.PENDING;
    private Predicate<InstanceTransformationContext> predicate = (c) -> true;

    public InstanceLifecyclePhase(@NonNull String name, @NonNull InstanceLifecyclePhaseAccess phaseAccess, @NonNull ExecutionFunction<L> function) {
        this.name = name;
        this.phaseAccess = phaseAccess;
        this.function = function;
    }

    private void setState(State state) {
        if (this.state == state) return;
        logger.debug(String.format("%-20s  %-10s ==> %s", "Phase '" + this.name + "':", this.state, state));
        this.state = state;
    }

    public void skip() {
        setState(State.SKIPPED);
    }

    public boolean shouldExecute(InstanceTransformationContext context) {
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

    public enum State {
        PENDING,
        SKIPPING,
        EXECUTING,
        DONE,
        SKIPPED,
        FAILED,
    }
}
