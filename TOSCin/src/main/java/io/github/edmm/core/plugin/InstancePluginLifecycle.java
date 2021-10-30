package io.github.edmm.core.plugin;

public interface InstancePluginLifecycle {
    void prepare();

    void transformToTOSCA();

    void storeTransformedTOSCA();

    void cleanup();
}
