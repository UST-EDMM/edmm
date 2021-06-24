package io.github.edmm.core.plugin;

public interface InstancePluginLifecycle {
    void prepare();

    void getModels();

    void transformDirectlyToTOSCA();

    void storeTransformedTOSCA();

    void cleanup();
}
