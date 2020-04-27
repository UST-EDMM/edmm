package io.github.edmm.plugins;

import java.io.File;

import io.github.edmm.core.plugin.AbstractLifecycleInstancePlugin;
import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import org.apache.commons.io.FileUtils;
import org.junit.After;

public abstract class InstancePluginTest {

    protected final File targetDirectory;

    public InstancePluginTest(File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    protected void executeLifecycle(InstancePlugin plugin, InstanceTransformationContext context) {
        AbstractLifecycleInstancePlugin lifecycle = plugin.getLifecycle(context);

        lifecycle.prepare();
        lifecycle.getModels();
        lifecycle.transformToEDIMM();
        lifecycle.transformToTOSCA();
        lifecycle.createYAML();
        lifecycle.cleanup();
    }

    @After
    public void destroy() throws Exception {
        if (targetDirectory != null) {
            FileUtils.deleteDirectory(targetDirectory);
        }
    }
}
