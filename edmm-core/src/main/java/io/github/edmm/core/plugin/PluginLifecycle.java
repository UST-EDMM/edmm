package io.github.edmm.core.plugin;

import io.github.edmm.core.plugin.support.LifecyclePhaseAccess;

public interface PluginLifecycle extends LifecyclePhaseAccess {

    void prepare();

    void transform();

    void cleanup();
}
