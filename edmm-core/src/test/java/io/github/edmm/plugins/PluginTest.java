package io.github.edmm.plugins;

import java.io.File;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.TransformationContext;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PluginTest {

    private static final Logger logger = LoggerFactory.getLogger(PluginTest.class);

    protected final File targetDirectory;

    public PluginTest(File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    protected void executeLifecycle(Plugin plugin, TransformationContext context) {
        AbstractLifecycle lifecycle = plugin.getLifecycle(context);
        lifecycle.checkModel();
        lifecycle.prepare();
        lifecycle.transform();
        lifecycle.cleanup();
    }

    @After
    public void destroy() throws Exception {
        if (targetDirectory != null) {
            logger.info("Clean up working directory...");
            FileUtils.deleteDirectory(targetDirectory);
        }
    }
}
