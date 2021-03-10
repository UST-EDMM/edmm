package io.github.edmm.plugins;

import java.nio.file.Files;

import io.github.edmm.plugins.kubernetes.KubernetesPlugin;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class KubernetesTests extends PluginTest {

    public KubernetesTests() throws Exception {
        super(new ClassPathResource("templates").getFile(), new ClassPathResource("templates/kubernetes.yml").getFile(), Files.createTempDirectory("kubernetes-").toFile());
    }

    @Test
    public void testLifecycleExecution() {
        executeLifecycle(new KubernetesPlugin());
    }
}
