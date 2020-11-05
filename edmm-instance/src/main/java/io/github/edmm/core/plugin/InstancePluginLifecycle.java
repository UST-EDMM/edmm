package io.github.edmm.core.plugin;

public interface InstancePluginLifecycle {
    void prepare();

    void getModels();

    void transformToEDMMi();

    void transformEdmmiToTOSCA();

    void transformDirectlyToTOSCA();

    void createYAML();

    void cleanup();
}
