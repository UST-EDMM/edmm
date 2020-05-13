package io.github.edmm.plugins;

import java.nio.file.Files;
import java.util.HashSet;

import io.github.edmm.core.execution.ExecutionContext;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.parameters.UserInput;
import io.github.edmm.plugins.terraform.TerraformPlugin;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class TerraformTests extends PluginTest {

    private static final Logger logger = LoggerFactory.getLogger(TerraformTests.class);

    private TransformationContext context;

    public TerraformTests() throws Exception {
        super(Files.createTempDirectory("terraform-").toFile());
    }

    @Before
    public void init() throws Exception {
        ClassPathResource sourceResource = new ClassPathResource("templates");
        ClassPathResource templateResource = new ClassPathResource("templates/scenario_paas_saas.yml");
        DeploymentModel model = DeploymentModel.of(templateResource.getFile());
        logger.info("Source directory is '{}'", sourceResource.getFile());
        logger.info("Target directory is '{}'", targetDirectory);
        context = new TransformationContext(model, TerraformPlugin.TERRAFORM, sourceResource.getFile(), targetDirectory);
    }

    @Test
    public void testLifecycleExecution() {
        executeLifecycle(new TerraformPlugin(), context);
    }

    @Test
    @Ignore
    public void testDeploymentExecution() throws Exception {
        testLifecycleExecution();
        TransformationContext context = TransformationContext.of(targetDirectory);
        ExecutionContext executionContext = new ExecutionContext(context);

        HashSet<UserInput> userInputs = new HashSet<>();
        userInputs.add(new UserInput("awsAccessKey", ""));
        userInputs.add(new UserInput("awsSecretKey", ""));

        executionContext.setUserInputs(userInputs);

        executeDeployment(new TerraformPlugin(), executionContext);
    }
}
