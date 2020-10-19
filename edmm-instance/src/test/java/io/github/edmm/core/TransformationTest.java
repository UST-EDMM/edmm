package io.github.edmm.core;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import io.github.edmm.core.yaml.EDMMiYamlParser;
import io.github.edmm.core.yaml.EDMMiYamlTransformer;
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
    private EDMMiYamlTransformer EDMMiYamlTransformer;

    @Test
    public void testCreateYamlForEDiMM() throws Exception {
        givenYamlOfNginxResource();

        whenTransformationIsDone();

        thenAssertTransformationFile();
    }

    private void givenYamlOfNginxResource() throws Exception {
        this.resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDMMi.yaml");
        this.deploymentInstance = new EDMMiYamlParser().parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());
        this.EDMMiYamlTransformer = new EDMMiYamlTransformer();
    }

    private void whenTransformationIsDone() throws Exception {
        EDMMiYamlTransformer.createYamlforEDiMM(deploymentInstance, Files.createTempDirectory("kubernetes-").toFile().getAbsolutePath());
        this.file = new File(EDMMiYamlTransformer.getFileOutputLocation());
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
