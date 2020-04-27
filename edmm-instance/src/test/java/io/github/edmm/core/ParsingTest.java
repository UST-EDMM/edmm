package io.github.edmm.core;

import io.github.edmm.core.yaml.YamlParser;
import io.github.edmm.model.edimm.DeploymentInstance;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertNotNull;

class ParsingTest {

    @Test
    void testParsingYaml() throws Exception {
        ClassPathResource resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDiMM.yaml");
        YamlParser yamlParser = new YamlParser();
        DeploymentInstance deploymentInstance = yamlParser.parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());
        assertNotNull(deploymentInstance);
    }
}
