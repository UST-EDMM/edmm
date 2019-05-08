package io.github.miwurster.edm.core.plugin;

import io.github.miwurster.edm.core.plugin.support.LifecyclePhaseAccess;

public interface PluginLifecycle extends LifecyclePhaseAccess {

    default boolean checkEnvironment() {
        return true;
    }

    default boolean checkModel() {
        return true;
    }

    void prepare();

    void transform();

    void cleanup();
}
