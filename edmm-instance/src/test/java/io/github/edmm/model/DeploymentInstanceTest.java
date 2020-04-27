package io.github.edmm.model;

import io.github.edmm.core.yaml.YamlParser;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.edimm.InstanceState;
import io.github.edmm.model.opentosca.TOSCAState;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

class DeploymentInstanceTest {

    @Test
    public void testDeploymentInstance() throws Exception {
        ClassPathResource resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDiMM.yaml");
        YamlParser yamlParser = new YamlParser();
        DeploymentInstance deploymentInstance = yamlParser.parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());

        assertNotNull(deploymentInstance);
        assertEquals("nginx-deployment", deploymentInstance.getName());
        assertNotNull(deploymentInstance.getVersion());
        assertEquals(InstanceState.InstanceStateForDeploymentInstance.CREATED, deploymentInstance.getState());
        assertEquals("92d76709-1fca-4d77-9865-c9cbf0dc7106", deploymentInstance.getId());
        assertEquals("2020-04-20T11:32:44.000+02:00", deploymentInstance.getCreatedAt());
        assertNull(deploymentInstance.getDescription());
        assertNull(deploymentInstance.getDeploymentModelLocation());
        assertEquals(5, deploymentInstance.getMetadata().size());
        assertEquals(5, deploymentInstance.getInstanceProperties().size());
        assertEquals(3, deploymentInstance.getComponentInstances().size());
    }

    @Test
    void testInstanceProperties() throws Exception {
        ClassPathResource resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDiMM.yaml");
        YamlParser yamlParser = new YamlParser();
        DeploymentInstance deploymentInstance = yamlParser.parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());

        assertEquals(3, deploymentInstance.getInstanceProperties().get(0).getInstanceValue());
        assertEquals(Long.class.getSimpleName(), deploymentInstance.getInstanceProperties().get(1).getType());
        assertNotNull(deploymentInstance.getInstanceProperties().get(2).getInstanceValue());
        assertEquals("updatedReplicas", deploymentInstance.getInstanceProperties().get(4).getKey());
    }

    @Test
    void testComponentInstances() throws Exception {
        ClassPathResource resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDiMM.yaml");
        YamlParser yamlParser = new YamlParser();
        DeploymentInstance deploymentInstance = yamlParser.parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());

        assertNotNull(deploymentInstance.getComponentInstances().get(0).getId());
        assertEquals("nginx-deployment-6b474476c4-22gtg", deploymentInstance.getComponentInstances().get(0).getName());
        assertEquals("nginx", deploymentInstance.getComponentInstances().get(0).getType());
        assertEquals(InstanceState.InstanceStateForComponentInstance.CREATED, deploymentInstance.getComponentInstances().get(0).getState());
        assertNotNull(deploymentInstance.getComponentInstances().get(0).getCreatedAt());
        assertNull(deploymentInstance.getComponentInstances().get(0).getArtifacts());
        assertNull(deploymentInstance.getComponentInstances().get(0).getOperations());
        assertEquals(10, deploymentInstance.getComponentInstances().get(0).getInstanceProperties().size());
        assertEquals("docker://56be2e934c89cb5667ee0674a596d116c8987fe1f06b5f83bc107bccec1c08a3", deploymentInstance.getComponentInstances().get(0).getInstanceProperties().get(1).getInstanceValue());
        assertNotNull(deploymentInstance.getComponentInstances().get(0).getInstanceProperties().get(2).getType());
        assertTrue(deploymentInstance.getComponentInstances().get(0).getInstanceProperties().get(3).getType() instanceof String);
        assertEquals("containerStatus::nginx::restartCount", deploymentInstance.getComponentInstances().get(0).getInstanceProperties().get(4).getKey());
        assertEquals(deploymentInstance.getComponentInstances().get(1).getState().toTOSCANodeTemplateInstanceState(), TOSCAState.NodeTemplateInstanceState.CREATED);
        assertTrue(deploymentInstance.getComponentInstances().get(0).getType().contains("nginx"));
        assertNull(deploymentInstance.getComponentInstances().get(0).getRelationInstances());
        assertEquals("containerStatus::nginx::image", deploymentInstance.getComponentInstances().get(2).getInstanceProperties().get(2).getKey());
        assertTrue(deploymentInstance.getComponentInstances().get(0).getMetadata().containsKey("app"));
        assertTrue(deploymentInstance.getComponentInstances().get(0).getMetadata().containsValue("default"));
        assertNotNull(deploymentInstance.getComponentInstances().get(0).getMetadata().get("pod-template-hash"));
        assertTrue(!deploymentInstance.getComponentInstances().get(0).getMetadata().isEmpty());
    }

    @Test
    void testMetadata() throws Exception {
        ClassPathResource resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDiMM.yaml");
        YamlParser yamlParser = new YamlParser();
        DeploymentInstance deploymentInstance = yamlParser.parseYamlAndTransformToDeploymentInstance(resource.getFile().getAbsolutePath());

        assertNull(deploymentInstance.getMetadata().get("resource"));
        assertTrue(deploymentInstance.getMetadata().containsKey("resourceVersion"));
        assertEquals(1, deploymentInstance.getMetadata().get("generation"));
        assertNotNull(deploymentInstance.getMetadata().get("app"));
        assertEquals("1", deploymentInstance.getMetadata().get("deployment.kubernetes.io/revision"));
    }
}
