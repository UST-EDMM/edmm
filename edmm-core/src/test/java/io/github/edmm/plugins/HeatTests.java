package io.github.edmm.plugins;

import java.nio.file.Files;

import io.github.edmm.core.transformation.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.plugins.heat.HeatPlugin;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class HeatTests extends PluginTest {

    private static final Logger logger = LoggerFactory.getLogger(HeatTests.class);

    private TransformationContext context;

    public HeatTests() throws Exception {
        super(Files.createTempDirectory("heat-").toFile());
    }

    @Before
    public void init() throws Exception {
        ClassPathResource sourceResource = new ClassPathResource("templates");
        ClassPathResource templateResource = new ClassPathResource("templates/scenario_iaas.yml");
        DeploymentModel model = DeploymentModel.of(templateResource.getFile());
        logger.info("Source directory is '{}'", sourceResource.getFile());
        logger.info("Target directory is '{}'", targetDirectory);
        context = new TransformationContext(model, TargetTechnology.NOOP, sourceResource.getFile(), targetDirectory);
    }

    @Test
    public void testLifecycleExecution() {
        executeLifecycle(new HeatPlugin(), context);
    }
}
