package io.github.edmm.core.plugin;

import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.plugin.support.LifecyclePhaseAccess;

public interface PluginLifecycle extends LifecyclePhaseAccess {

    CheckModelResult checkModel();

    void prepare();

    void transform();

    void cleanup();
}
