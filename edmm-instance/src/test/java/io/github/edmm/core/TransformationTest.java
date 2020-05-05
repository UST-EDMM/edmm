package io.github.edmm.core;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import io.github.edmm.core.yaml.YamlParser;
import io.github.edmm.core.yaml.YamlTransformer;
import io.github.edmm.model.edimm.DeploymentInstance;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TransformationTest {

    private File file;
    private ClassPathResource resource;
    private DeploymentInstance deploymentInstance;
    private YamlTransformer yamlTransformer;

    @Test
    public void testCreateYamlForEDiMM() throws Exception {
        givenYamlOfNginxResource();

        whenTransformationIsDone();

        thenAssertTransformationFile();
    }

    private void givenYamlOfNginxResource() throws Exception {
        this.resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDiMM.yaml");
        this.deploymentInstance = new YamlParser().parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());
        this.yamlTransformer = new YamlTransformer();
    }

    private void whenTransformationIsDone() throws Exception {
        yamlTransformer.createYamlforEDiMM(deploymentInstance, Files.createTempDirectory("kubernetes-").toFile().getAbsolutePath());
        this.file = new File(yamlTransformer.getFileOutputLocation());
    }

    private void thenAssertTransformationFile() throws Exception {
        String expectedString = FileUtils.readFileToString(this.resource.getFile(), StandardCharsets.UTF_8);
        String actualString = FileUtils.readFileToString(this.file, StandardCharsets.UTF_8);

        assertNotNull(this.deploymentInstance);
        assertTrue(this.file.exists());
        assertTrue(this.file.length() > 0);
        assertEquals(expectedString, actualString);
    }

    @After
    public void destroy() throws Exception {
        if (this.file != null) {
            FileUtils.deleteDirectory(this.file);
        }
    }
}
