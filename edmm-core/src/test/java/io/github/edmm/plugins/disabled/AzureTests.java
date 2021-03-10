package io.github.edmm.plugins.disabled;

import java.io.IOException;
import java.nio.file.Files;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.plugins.PluginTest;
import io.github.edmm.plugins.azure.AzurePlugin;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class AzureTests extends PluginTest {

    private TransformationContext context;

    public AzureTests() throws Exception {
        super(new ClassPathResource("templates").getFile(), new ClassPathResource("templates/scenario_iaas.yml").getFile(), Files.createTempDirectory("azure-").toFile());
    }

    @Before
    public void init() throws Exception {
        context = new TransformationContext(inputFile, DeploymentTechnology.NOOP, repositoryDirectory, targetDirectory);
    }

    @Test
    @Ignore
    public void testLifecycleExecution() {
        executeLifecycle(new AzurePlugin(), context);
    }
}
