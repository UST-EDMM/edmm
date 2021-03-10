package io.github.edmm.plugins;

import java.nio.file.Files;

import io.github.edmm.plugins.terraform.TerraformPlugin;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class TerraformTests extends PluginTest {

    public TerraformTests() throws Exception {
        super(new ClassPathResource("templates").getFile(), new ClassPathResource("templates/scenario_iaas_single_compute.yml").getFile(), Files.createTempDirectory("terraform-").toFile());
    }

    @Test
    public void testLifecycleExecution() {
        executeLifecycle(new TerraformPlugin());
    }
}
