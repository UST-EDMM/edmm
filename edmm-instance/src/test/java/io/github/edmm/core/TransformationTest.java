package io.github.edmm.core;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import io.github.edmm.core.parser.YamlParser;
import io.github.edmm.core.parser.YamlTransformer;
import io.github.edmm.model.edimm.DeploymentInstance;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

class TransformationTest {

    File file;

    @Test
    void testCreateYamlForEDiMM() throws Exception {
        ClassPathResource resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDiMM.yaml");
        DeploymentInstance deploymentInstance = new YamlParser().parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());
        YamlTransformer yamlTransformer = new YamlTransformer();
        yamlTransformer.createYamlforEDiMM(deploymentInstance, Files.createTempDirectory("kubernetes-").toFile().getAbsolutePath());
        this.file = new File(yamlTransformer.getFileOutputLocation());

        String expectedString = FileUtils.readFileToString(resource.getFile(), StandardCharsets.UTF_8);
        String actualString = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        assertNotNull(deploymentInstance);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertEquals(expectedString, actualString);
    }

    @After
    public void destroy() throws Exception {
        if (this.file != null) {
            FileUtils.deleteDirectory(this.file);
        }
    }
}
