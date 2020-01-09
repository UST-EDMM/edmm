package io.github.edmm.plugins;

import java.io.File;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TransformationContext;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.core.plugin.support.CheckModelResult.State.OK;

public abstract class PluginTest {

    private static final Logger logger = LoggerFactory.getLogger(PluginTest.class);

    protected final File targetDirectory;

    public PluginTest(File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    protected void executeLifecycle(Plugin plugin, TransformationContext context) {
        AbstractLifecycle lifecycle = plugin.getLifecycle(context);
        CheckModelResult result = lifecycle.checkModel();
        logger.info("checkModel(): state={}, unsupportedComponents={}",
                result.getState(), result.getUnsupportedComponents());
        if (OK.equals(result.getState())) {
            lifecycle.prepare();
            lifecycle.transform();
            lifecycle.cleanup();
        } else {
            logger.warn("Skip execution due to unsupported components...");
        }
    }

    @After
    public void destroy() throws Exception {
        if (targetDirectory != null) {
            logger.info("Clean up working directory...");
            FileUtils.deleteDirectory(targetDirectory);
        }
    }
}
