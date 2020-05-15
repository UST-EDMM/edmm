package io.github.edmm.plugins;

import java.nio.file.Files;

import io.github.edmm.core.execution.ExecutionContext;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.plugins.kubernetes.KubernetesPlugin;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class KubernetesTests extends PluginTest {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesTests.class);

    private TransformationContext context;

    public KubernetesTests() throws Exception {
        super(Files.createTempDirectory("kubernetes-").toFile());
    }

    @Before
    public void init() throws Exception {
        ClassPathResource sourceResource = new ClassPathResource("templates");
        ClassPathResource templateResource = new ClassPathResource("templates/kubernetes.yml");
        logger.info("Source directory is '{}'", sourceResource.getFile());
        logger.info("Target directory is '{}'", targetDirectory);
        context = new TransformationContext(templateResource.getFile(), KubernetesPlugin.KUBERNETES, sourceResource.getFile(), targetDirectory);
    }

    @Test
    public void testLifecycleExecution() {
        executeLifecycle(new KubernetesPlugin(), context);
    }

    @Test
    @Ignore
    public void testDeploymentExecution() {
        testLifecycleExecution();
        TransformationContext context = TransformationContext.of(targetDirectory);
        executeDeployment(new KubernetesPlugin(), new ExecutionContext(context));
    }
}
