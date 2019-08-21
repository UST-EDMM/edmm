package io.github.edmm.plugins.ansible;

import java.io.File;
import java.nio.file.Files;

import io.github.edmm.core.transformation.Transformation;
import io.github.edmm.core.transformation.TransformationContext;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.mock;

public class AnsibleLifecycleTest {

    private static final Logger logger = LoggerFactory.getLogger(AnsibleLifecycleTest.class);

    @Test
    public void run() throws Exception {
        Transformation transformation = mock(Transformation.class);
        File rootDirectory = Files.createTempDirectory("ansible-").toFile();
        logger.info("Root directory is '{}'", rootDirectory);
        TransformationContext context = new TransformationContext(transformation, rootDirectory);
        AnsibleLifecycle lifecycle = new AnsibleLifecycle(context);
        lifecycle.prepare();
        lifecycle.transform();
        lifecycle.cleanup();
    }
}
