package io.github.edmm.plugins;

import java.nio.file.Files;

import io.github.edmm.plugins.compose.DockerComposePlugin;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

public class DockerComposeTests extends PluginTest {

    public DockerComposeTests() throws Exception {
        super(new ClassPathResource("templates").getFile(), new ClassPathResource("templates/scenario_iaas.yml").getFile(), Files.createTempDirectory("compose-").toFile());
    }

    @Test
    public void testLifecycleExecution() {
        executeLifecycle(new DockerComposePlugin());
    }
}
