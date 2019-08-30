package io.github.edmm.plugins.azure;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.transformation.Transformation;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.plugins.PluginTest;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AzurePluginTest extends PluginTest {
    private static final Logger logger = LoggerFactory.getLogger(AzurePluginTest.class);
    private TransformationContext context;

    public AzurePluginTest() throws IOException {
        super(Files.createTempDirectory("azure-").toFile());
    }

    @Before
    public void init() throws Exception {
        ClassPathResource sourceResource = new ClassPathResource("templates");
        ClassPathResource templateResource = new ClassPathResource("templates/scenario_iaas.yml");
        DeploymentModel model = DeploymentModel.of(templateResource.getFile());
        Transformation transformation = mock(Transformation.class);
        when(transformation.getModel()).thenReturn(model);
        logger.info("Source directory is '{}'", sourceResource.getFile());
        logger.info("Target directory is '{}'", targetDirectory);
        context = new TransformationContext(transformation, sourceResource.getFile(), targetDirectory);
    }

    @Test
    public void test() throws Exception {
        executeLifecycle(new AzurePlugin(), context);
    }

}