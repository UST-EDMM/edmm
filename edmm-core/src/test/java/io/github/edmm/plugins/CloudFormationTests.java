package io.github.edmm.plugins;

import java.nio.file.Files;

import io.github.edmm.plugins.cfn.CloudFormationPlugin;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

public class CloudFormationTests extends PluginTest {

    public CloudFormationTests() throws Exception {
        super(new ClassPathResource("templates").getFile(), new ClassPathResource("templates/scenario_paas_saas.yml").getFile(), Files.createTempDirectory("cfn-").toFile());
    }

    @Test
    public void testLifecycleExecution() {
        executeLifecycle(new CloudFormationPlugin());
    }
}
