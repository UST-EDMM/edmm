package io.github.edmm.core.plugin;

import io.github.edmm.core.TargetTechnology;
import io.github.edmm.core.execution.ExecutionContext;

public interface ExecutionPlugin {

    default void init() {
        // default
    }

    void execute(ExecutionContext context) throws Exception;

    default void finalize(ExecutionContext context) {
        // default
    }

    TargetTechnology getTargetTechnology();
}
