package io.github.edmm.plugins;

import java.io.File;
import java.nio.file.Files;

import io.github.edmm.core.transformation.Transformation;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.plugins.chef.ChefLifecycle;
import io.github.edmm.plugins.chef.ChefPlugin;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChefTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChefTests.class);

    private File targetDirectory;
    private TransformationContext context;

    @Before
    public void init() throws Exception {
        targetDirectory = Files.createTempDirectory("chef-").toFile();
        ClassPathResource sourceResource = new ClassPathResource("templates");
        ClassPathResource templateResource = new ClassPathResource("templates/scenario_iaas.yml");
        DeploymentModel model = DeploymentModel.of(templateResource.getFile());
        Transformation transformation = mock(Transformation.class);
        when(transformation.getModel()).thenReturn(model);
        LOGGER.info("Source directory is '{}'", sourceResource.getFile());
        LOGGER.info("Target directory is '{}'", targetDirectory);
        context = new TransformationContext(transformation, sourceResource.getFile(), targetDirectory);
    }

    @Test
    public void test() {
        // Setup plugin
        ChefPlugin plugin = new ChefPlugin();
        ChefLifecycle lifecycle = plugin.getLifecycle(context);
        // Execute lifecycle phases
        lifecycle.checkEnvironment();
        lifecycle.checkModel();
        lifecycle.prepare();
        lifecycle.transform();
        lifecycle.cleanup();
    }

    @After
    public void destroy() throws Exception {
        FileUtils.deleteDirectory(targetDirectory);
    }
}
