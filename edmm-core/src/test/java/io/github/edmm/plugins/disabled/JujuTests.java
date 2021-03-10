package io.github.edmm.plugins.disabled;

import java.nio.file.Files;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.plugins.PluginTest;
import io.github.edmm.plugins.juju.JujuPlugin;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class JujuTests extends PluginTest {

    private TransformationContext context;

    public JujuTests() throws Exception {
        super(new ClassPathResource("templates").getFile(), new ClassPathResource("templates/scenario_iaas_single_compute.yml").getFile(), Files.createTempDirectory("juju-").toFile());
    }

    @Before
    public void init() throws Exception {
        context = new TransformationContext(inputFile, DeploymentTechnology.NOOP, repositoryDirectory, targetDirectory);
    }

    @Test
    @Ignore
    public void testLifecycleExecution() {
        executeLifecycle(new JujuPlugin(), context);
    }
}
