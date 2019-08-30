package io.github.edmm.plugins.azure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.transformation.Transformation;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AzureLifeCycleTest {
    private static final Logger logger = LoggerFactory.getLogger(AzureLifeCycleTest.class);

    private File rootDirectory;
    private TransformationContext context;

    @Before
    public void init() throws Exception {
        rootDirectory = Files.createTempDirectory("azure-").toFile();
    }

    private void initTest(String modelFile) throws IOException {
        ClassPathResource resource = new ClassPathResource(modelFile);
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Transformation transformation = mock(Transformation.class);
        when(transformation.getModel()).thenReturn(model);
        logger.info("Root directory is '{}'", rootDirectory);
        context = new TransformationContext(transformation, rootDirectory);
        PluginFileAccess fileAccess = context.getFileAccess();
    }

    @Test
    public void test() throws Exception {
        initTest("templates/one_stack/definitions.yml");
        AzureLifeCycle lifeCycle = new AzureLifeCycle(context);
        lifeCycle.transform();
    }

    @After
    public void destroy() throws Exception {
        FileUtils.deleteDirectory(rootDirectory);
    }
}