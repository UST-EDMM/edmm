package io.github.edmm.plugins;

import java.nio.file.Files;

import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.plugins.kubernetes.KubernetesInstancePlugin;

import org.junit.Before;
import org.junit.Test;

public class KubernetesInstancePluginTest extends InstancePluginTest {

    private InstanceTransformationContext context;

    public KubernetesInstancePluginTest() throws Exception {
        super(Files.createTempDirectory("kubernetes-").toFile());
    }

    @Before
    public void init() {
        context = new InstanceTransformationContext(SourceTechnology.builder().id("kubernetes").name("Kubernetes").build(), this.targetDirectory + "/kubernetes-");
    }

    @Test
    public void testLifecycleExecution() {
        executeLifecycle(new KubernetesInstancePlugin(), context);
    }
}