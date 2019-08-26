package io.github.edmm.plugins.terraform;

import java.io.File;
import java.nio.file.Files;

import io.github.edmm.core.transformation.Transformation;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TerraformTests {

    private static final Logger logger = LoggerFactory.getLogger(TerraformTests.class);

    private TransformationContext context;

    @Before
    public void init() throws Exception {
        File rootDirectory = Files.createTempDirectory("terraform-").toFile();
        ClassPathResource resource = new ClassPathResource("templates/scenario_iaas.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Transformation transformation = mock(Transformation.class);
        when(transformation.getModel()).thenReturn(model);
        logger.info("Root directory is '{}'", rootDirectory);
        context = new TransformationContext(transformation, rootDirectory);
    }

    @Test
    public void test() {
        TerraformPlugin plugin = new TerraformPlugin();
        TerraformLifecycle lifecycle = plugin.getLifecycle(context);
        lifecycle.checkEnvironment();
        lifecycle.checkModel();
        lifecycle.prepare();
        lifecycle.transform();
        lifecycle.cleanup();
    }
}
