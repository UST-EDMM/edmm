package io.github.edmm.plugins.disabled;

import java.nio.file.Files;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.plugins.PluginTest;
import io.github.edmm.plugins.cloudify.CloudifyPlugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

public class CloudifyTests extends PluginTest {

    private TransformationContext context;

    public CloudifyTests() throws Exception {
        super(new ClassPathResource("templates").getFile(), new ClassPathResource("templates/scenario_iaas.yml").getFile(), Files.createTempDirectory("cloudify-").toFile());
    }

    @BeforeEach
    public void init() throws Exception {
        context = new TransformationContext(inputFile, DeploymentTechnology.NOOP, repositoryDirectory, targetDirectory);
    }

    @Test
    @Disabled
    public void testLifecycleExecution() {
        executeLifecycle(new CloudifyPlugin(), context);
    }
}
