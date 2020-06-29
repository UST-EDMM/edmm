package io.github.edmm.core.plugin;

import io.github.edmm.core.plugin.support.InstanceLifecyclePhaseAccess;

public interface InstancePluginLifecycle extends InstanceLifecyclePhaseAccess {
    void prepare();

    void getModels();

    void transformToEDIMM();

    void transformToTOSCA();

    void createYAML();

    void cleanup();
}
