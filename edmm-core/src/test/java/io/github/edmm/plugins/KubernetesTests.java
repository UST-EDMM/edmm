package io.github.edmm.plugins;

import java.nio.file.Files;

import io.github.edmm.core.transformation.Platform;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.plugins.kubernetes.KubernetesPlugin;
import org.junit.Before;
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
        ClassPathResource templateResource = new ClassPathResource("templates/scenario_iaas_single_compute.yml");
        DeploymentModel model = DeploymentModel.of(templateResource.getFile());
        logger.info("Source directory is '{}'", sourceResource.getFile());
        logger.info("Target directory is '{}'", targetDirectory);
        context = new TransformationContext(model, Platform.NOOP, sourceResource.getFile(), targetDirectory);
    }

    @Test
    public void testLifecycleExecution() {
        executeLifecycle(new KubernetesPlugin(), context);
    }
}
