package io.github.edmm.core;

import java.io.File;
import java.nio.charset.StandardCharsets;

import io.github.edmm.core.parser.YamlParser;
import io.github.edmm.core.parser.YamlTransformer;
import io.github.edmm.model.edimm.DeploymentInstance;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

class TransformationTest {

    @Test
    void testCreateYamlForEDiMM() throws Exception {
        ClassPathResource resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDiMM.yaml");
        DeploymentInstance deploymentInstance = new YamlParser().parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());
        YamlTransformer yamlTransformer = new YamlTransformer();
        yamlTransformer.createYamlforEDiMM(deploymentInstance, "test-");
        File file = new File(yamlTransformer.getFileOutputLocation());

        String expectedString = FileUtils.readFileToString(resource.getFile(), StandardCharsets.UTF_8);
        String actualString = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        assertNotNull(deploymentInstance);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertEquals(expectedString, actualString);
    }
}
