package io.github.edmm.plugins;

import java.nio.file.Files;

import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.plugins.kubernetes.KubernetesInstancePlugin;
import org.junit.Before;
import org.junit.Test;

public class CfnInstancePluginTest extends InstancePluginTest {

    private InstanceTransformationContext context;

    public CfnInstancePluginTest() throws Exception {
        super(Files.createTempDirectory("cfn-").toFile());
    }

    @Before
    public void init() {
        context = new InstanceTransformationContext(SourceTechnology.builder().id("cfn").name("CFN").build(), this.targetDirectory + "/cfn-");
    }

    @Test
    public void testLifecycleExecution() {
        executeLifecycle(new KubernetesInstancePlugin(), context);
    }
}
