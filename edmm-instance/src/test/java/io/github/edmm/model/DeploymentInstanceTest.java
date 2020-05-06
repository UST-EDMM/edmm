package io.github.edmm.model;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import io.github.edmm.core.yaml.YamlParser;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.edimm.InstanceState;
import io.github.edmm.model.edimm.RelationInstance;
import io.github.edmm.model.edimm.RelationTypes;
import io.github.edmm.model.opentosca.TOSCAState;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DeploymentInstanceTest {

    ClassPathResource resource;
    YamlParser yamlParser;
    DeploymentInstance deploymentInstance;

    @Test
    public void testDeploymentInstance() throws Exception {
        givenYamlOfNginxResource();

        whenDeploymentInstanceCreated();

        thenAssertDeploymentInstance();
    }

    private void givenYamlOfNginxResource() {
        this.resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDiMM.yaml");
        this.yamlParser = new YamlParser();
    }

    private void whenDeploymentInstanceCreated() throws IOException {
        this.deploymentInstance = this.yamlParser.parseYamlAndTransformToDeploymentInstance(this.resource.getFile().getAbsolutePath());
    }

    private void thenAssertDeploymentInstance() {
        assertNotNull(this.deploymentInstance);
        assertEquals("nginx-deployment", this.deploymentInstance.getName());
        assertNotNull(this.deploymentInstance.getVersion());
        assertEquals(InstanceState.InstanceStateForDeploymentInstance.CREATED, this.deploymentInstance.getState());
        assertEquals("92d76709-1fca-4d77-9865-c9cbf0dc7106", this.deploymentInstance.getId());
        assertEquals("2020-04-20T11:32:44.000+02:00", this.deploymentInstance.getCreatedAt());
        assertNull(this.deploymentInstance.getDescription());
        assertEquals(5, this.deploymentInstance.getMetadata().size());
        assertEquals(5, this.deploymentInstance.getInstanceProperties().size());
        assertEquals(3, this.deploymentInstance.getComponentInstances().size());
    }

    @Test
    public void testInstanceProperties() throws Exception {
        givenYamlOfNginxResource();

        whenDeploymentInstanceCreated();

        thenAssertInstanceProperties();
    }

    private void thenAssertInstanceProperties() {
        assertEquals(3, this.deploymentInstance.getInstanceProperties().get(0).getInstanceValue());
        assertEquals(Long.class.getSimpleName(), this.deploymentInstance.getInstanceProperties().get(1).getType());
        assertNotNull(this.deploymentInstance.getInstanceProperties().get(2).getInstanceValue());
        assertEquals("updatedReplicas", this.deploymentInstance.getInstanceProperties().get(4).getKey());
    }

    @Test
    public void testComponentInstances() throws Exception {
        givenYamlOfNginxResource();

        whenDeploymentInstanceCreated();

        thenAssertComponentInstances();
    }

    private void thenAssertComponentInstances() {
        assertNotNull(this.deploymentInstance.getComponentInstances().get(0).getId());
        assertEquals("nginx-deployment-6b474476c4-22gtg", this.deploymentInstance.getComponentInstances().get(0).getName());
        assertEquals("nginx", this.deploymentInstance.getComponentInstances().get(0).getType());
        assertEquals(InstanceState.InstanceStateForComponentInstance.CREATED, this.deploymentInstance.getComponentInstances().get(0).getState());
        assertNotNull(this.deploymentInstance.getComponentInstances().get(0).getCreatedAt());
        assertEquals(10, this.deploymentInstance.getComponentInstances().get(0).getInstanceProperties().size());
        assertEquals("docker://56be2e934c89cb5667ee0674a596d116c8987fe1f06b5f83bc107bccec1c08a3", this.deploymentInstance.getComponentInstances().get(0).getInstanceProperties().get(1).getInstanceValue());
        assertNotNull(this.deploymentInstance.getComponentInstances().get(0).getInstanceProperties().get(2).getType());
        assertEquals("containerStatus::nginx::restartCount", this.deploymentInstance.getComponentInstances().get(0).getInstanceProperties().get(4).getKey());
        assertEquals(this.deploymentInstance.getComponentInstances().get(1).getState().toTOSCANodeTemplateInstanceState(), TOSCAState.NodeTemplateInstanceState.CREATED);
        assertTrue(this.deploymentInstance.getComponentInstances().get(0).getType().contains("nginx"));
        assertEquals(Collections.emptyList(), this.deploymentInstance.getComponentInstances().get(0).getRelationInstances());
        assertEquals("containerStatus::nginx::image", this.deploymentInstance.getComponentInstances().get(2).getInstanceProperties().get(2).getKey());
        assertTrue(this.deploymentInstance.getComponentInstances().get(0).getMetadata().containsKey("app"));
        assertTrue(this.deploymentInstance.getComponentInstances().get(0).getMetadata().containsValue("default"));
        assertNotNull(this.deploymentInstance.getComponentInstances().get(0).getMetadata().get("pod-template-hash"));
        assertTrue(!this.deploymentInstance.getComponentInstances().get(0).getMetadata().isEmpty());
    }

    @Test
    public void testRelationInstances() throws Exception {
        givenYamlOfTestStackResource();

        whenDeploymentInstanceCreated();

        thenAssertRelationInstances();
    }

    private void givenYamlOfTestStackResource() {
        this.resource = new ClassPathResource("deployments/unit-tests/teststackmore_EDiMM.yaml");
        this.yamlParser = new YamlParser();
    }

    private void thenAssertRelationInstances() {
        List<RelationInstance> relationInstances = this.deploymentInstance.getComponentInstances().get(1).getRelationInstances();
        RelationInstance relationInstance = relationInstances.get(0);

        assertNotNull(relationInstance);
        assertEquals("4aeb6497-90e8-4c68-99b4-dea09d4848da", relationInstance.getTargetInstanceId());
        assertEquals(RelationTypes.RelationType.dependsOn, relationInstance.getType());
        assertEquals("dependsOn::0", relationInstance.getId());
    }

    @Test
    public void testMetadata() throws Exception {
        givenYamlOfNginxResource();

        whenDeploymentInstanceCreated();

        thenAssertMetadata();
    }

    private void thenAssertMetadata() {
        assertNull(this.deploymentInstance.getMetadata().get("resource"));
        assertTrue(this.deploymentInstance.getMetadata().containsKey("resourceVersion"));
        assertEquals(1, this.deploymentInstance.getMetadata().get("generation"));
        assertNotNull(this.deploymentInstance.getMetadata().get("app"));
        assertEquals("1", this.deploymentInstance.getMetadata().get("deployment.kubernetes.io/revision"));
    }
}
