package io.github.edmm.plugins;

import java.io.File;
import java.nio.file.Files;

import io.github.edmm.core.execution.ExecutionContext;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.plugins.kubernetes.KubernetesPlugin;
import io.github.edmm.utils.Env;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class KubernetesTests extends PluginTest {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesTests.class);

    private TransformationContext context;

    public KubernetesTests() throws Exception {
        super(Files.createTempDirectory("kubernetes-").toFile());
    }

    @Before
    public void init() throws Exception {
        File repository;
        File inputFile;
        File outputDirectory;
        String repositoryValue = Env.get("REPOSITORY", null);
        String inputFileValue = Env.get("INPUT_FILE", null);
        String outputDirectoryValue = Env.get("OUTPUT_DIR", null);
        if (repositoryValue != null && inputFileValue != null && outputDirectoryValue != null) {
            repository = new File(repositoryValue);
            inputFile = new File(inputFileValue);
            outputDirectory = new File(outputDirectoryValue);
        } else {
            repository = new ClassPathResource("templates").getFile();
            inputFile = new ClassPathResource("templates/kubernetes.yml").getFile();
            outputDirectory = targetDirectory;
        }
        logger.info("Source directory is '{}'", repository);
        logger.info("Target directory is '{}'", outputDirectory);
        logger.info("Input file at '{}'", inputFile);
        context = new TransformationContext(inputFile, KubernetesPlugin.KUBERNETES, repository, outputDirectory);
    }

    @Test
    public void testLifecycleExecution() {
        executeLifecycle(new KubernetesPlugin(), context);
    }

    @Test
    @Ignore
    public void testDeploymentExecution() throws Exception {
        testLifecycleExecution();
        TransformationContext context = TransformationContext.of(targetDirectory);
        executeDeployment(new KubernetesPlugin(), new ExecutionContext(context));
    }
}
