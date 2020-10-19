package io.github.edmm.core;

import io.github.edmm.core.yaml.EDMMiYamlParser;
import io.github.edmm.model.edimm.DeploymentInstance;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertNotNull;

public class ParsingTest {
    private ClassPathResource resource;
    private EDMMiYamlParser EDMMiYamlParser;
    private DeploymentInstance deploymentInstance;

    @Test
    public void testParsingYaml() throws Exception {
        givenYamlOfNginxResource();

        whenDeploymentInstanceCreated();

        thenAssertDeploymentInstance();
    }

    private void givenYamlOfNginxResource() {
        this.resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDMMi.yaml");
        this.EDMMiYamlParser = new EDMMiYamlParser();
    }

    private void whenDeploymentInstanceCreated() throws Exception {
        this.deploymentInstance = EDMMiYamlParser.parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());
    }

    private void thenAssertDeploymentInstance() {
        assertNotNull(this.deploymentInstance);
    }
}
