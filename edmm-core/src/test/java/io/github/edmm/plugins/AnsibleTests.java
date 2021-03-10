package io.github.edmm.plugins;

import java.nio.file.Files;

import io.github.edmm.plugins.ansible.AnsiblePlugin;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class AnsibleTests extends PluginTest {

    public AnsibleTests() throws Exception {
        super(new ClassPathResource("templates").getFile(), new ClassPathResource("templates/scenario_iaas.yml").getFile(), Files.createTempDirectory("ansible-").toFile());
    }

    @Test
    public void testLifecycleExecution() {
        executeLifecycle(new AnsiblePlugin());
    }
}
