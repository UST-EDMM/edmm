package io.github.edmm.core;

import io.github.edmm.core.yaml.YamlParser;
import io.github.edmm.model.edimm.DeploymentInstance;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertNotNull;

public class ParsingTest {
    private ClassPathResource resource;
    private YamlParser yamlParser;
    private DeploymentInstance deploymentInstance;

    @Test
    public void testParsingYaml() throws Exception {
        givenYamlOfNginxResource();

        whenDeploymentInstanceCreated();

        thenAssertDeploymentInstance();
    }

    private void givenYamlOfNginxResource() {
        this.resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDMMi.yaml");
        this.yamlParser = new YamlParser();
    }

    private void whenDeploymentInstanceCreated() throws Exception {
        this.deploymentInstance = yamlParser.parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());
    }

    private void thenAssertDeploymentInstance() {
        assertNotNull(this.deploymentInstance);
    }
}
