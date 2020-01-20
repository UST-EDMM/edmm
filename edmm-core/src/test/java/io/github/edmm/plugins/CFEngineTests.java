package io.github.edmm.plugins;

import io.github.edmm.core.transformation.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.plugins.cfengine.CFEnginePlugin;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;

public class CFEngineTests extends PluginTest {

    private static final Logger logger = LoggerFactory.getLogger(CFEngineTests.class);

    private TransformationContext context;

    public CFEngineTests() throws Exception {
        super(Files.createTempDirectory("cfengine-").toFile());
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
        executeLifecycle(new CFEnginePlugin(), context);
    }
}
