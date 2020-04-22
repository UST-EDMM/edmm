package io.github.edmm.core;

import io.github.edmm.core.parser.YamlParser;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.TOSCAState;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ParsingTest {

    @Test
    public void testDeploymentInstance() throws Exception {
        ClassPathResource resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDiMM.yaml");
        YamlParser yamlParser = new YamlParser();
        DeploymentInstance deploymentInstance = yamlParser.parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());

        assertEquals(deploymentInstance.getName(), "nginx-deployment");
        assertEquals(deploymentInstance.getComponentInstances().size(), 3);
        assertNull(deploymentInstance.getDeploymentModelLocation());
    }

    @Test
    public void testInstanceProperties() throws Exception {
        ClassPathResource resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDiMM.yaml");
        YamlParser yamlParser = new YamlParser();
        DeploymentInstance deploymentInstance = yamlParser.parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());

        assertEquals(deploymentInstance.getInstanceProperties().get(0).getInstanceValue(), 3);
        assertEquals(deploymentInstance.getComponentInstances().get(2).getInstanceProperties().get(2).getKey(), "containerStatus::nginx::image");
    }

    @Test
    public void testComponentInstances() throws Exception {
        ClassPathResource resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDiMM.yaml");
        YamlParser yamlParser = new YamlParser();
        DeploymentInstance deploymentInstance = yamlParser.parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());

        assertEquals(deploymentInstance.getComponentInstances().get(1).getState().toTOSCANodeTemplateInstanceState(), TOSCAState.NodeTemplateInstanceState.CREATED);
        assertTrue(deploymentInstance.getComponentInstances().get(0).getType().contains("nginx"));
        assertNull(deploymentInstance.getComponentInstances().get(0).getRelationInstances());
        assertNull(deploymentInstance.getComponentInstances().get(1).getArtifacts());
    }

    @Test
    public void testMetadata() throws Exception {
        ClassPathResource resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDiMM.yaml");
        YamlParser yamlParser = new YamlParser();
        DeploymentInstance deploymentInstance = yamlParser.parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());

        assertNull(deploymentInstance.getMetadata().get("resource"));
        assertTrue(deploymentInstance.getMetadata().containsKey("resourceVersion"));
    }
}
