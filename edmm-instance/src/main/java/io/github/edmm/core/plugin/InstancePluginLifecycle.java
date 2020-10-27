package io.github.edmm.core.plugin;

public interface InstancePluginLifecycle {
    void prepare();

    void getModels();

    void transformToEDMMi();

    void transformToTOSCA();

    void createYAML();

    void cleanup();
}
