package io.github.edmm.model;

import java.util.List;

import javax.xml.namespace.QName;

import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.yaml.YamlParser;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.NodeTemplateInstance;
import io.github.edmm.model.opentosca.RelationshipTemplateInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;
import io.github.edmm.model.opentosca.TOSCAProperty;
import io.github.edmm.model.opentosca.TOSCAState;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

class ServiceTemplateInstanceTest {

    private ClassPathResource resource;
    private YamlParser yamlParser;
    private ServiceTemplateInstance serviceTemplateInstance;
    private DeploymentInstance deploymentInstance;

    @Test
    public void testServiceTemplate() throws Exception {
        givenYamlOfNginxResource();

        whenServiceTemplateInstanceCreated();

        thenAssertServiceTemplateInstance();
    }

    private void givenYamlOfNginxResource() {
        this.resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDiMM.yaml");
        this.yamlParser = new YamlParser();
    }

    private void whenServiceTemplateInstanceCreated() throws Exception {
        this.deploymentInstance = this.yamlParser.parseYamlAndTransformToDeploymentInstance(this.resource.getFile().getAbsolutePath());
        this.serviceTemplateInstance = new TOSCATransformer().transformEDiMMToServiceTemplateInstance(this.deploymentInstance);
    }

    private void thenAssertServiceTemplateInstance() {
        assertNotNull(this.serviceTemplateInstance);
        assertEquals("2020-04-20T11:32:44.000+02:00", this.serviceTemplateInstance.getCreatedAt());
        assertEquals(3, this.serviceTemplateInstance.getNodeTemplateInstances().size());
        assertEquals("nginx-deployment", this.serviceTemplateInstance.getCsarId());
        assertEquals(TOSCAState.ServiceTemplateInstanceState.CREATED, this.serviceTemplateInstance.getState());
        assertEquals(new QName("http://www.opentosca.org/servicetemplates", "nginx-deployment"), this.serviceTemplateInstance.getServiceTemplateId());
        assertEquals("92d76709-1fca-4d77-9865-c9cbf0dc7106", this.serviceTemplateInstance.getServiceTemplateInstanceId());
    }

    @Test
    public void testNodeTemplateInstances() throws Exception {
        givenYamlOfNginxResource();

        whenServiceTemplateInstanceCreated();

        thenAssertNodeTemplateInstances();
    }

    private void thenAssertNodeTemplateInstances() {
        List<NodeTemplateInstance> nodeTemplateInstances = this.serviceTemplateInstance.getNodeTemplateInstances();

        assertNotNull(nodeTemplateInstances);
        assertFalse(nodeTemplateInstances.isEmpty());
        assertNotNull(nodeTemplateInstances.get(0).getInstanceProperties());
        assertEquals(new QName("http://www.opentosca.org/nodetemplates", "nginx-deployment-6b474476c4-22gtg"), nodeTemplateInstances.get(0).getNodeTemplateId());
        assertEquals(new QName("http://www.opentosca.org/nodetypes", "nginx"), nodeTemplateInstances.get(0).getNodeType());
        assertEquals("a3ec9267-aff0-4484-ad5e-785502c9d2d5", nodeTemplateInstances.get(0).getNodeTemplateInstanceId());
        assertEquals("92d76709-1fca-4d77-9865-c9cbf0dc7106", nodeTemplateInstances.get(0).getServiceTemplateInstanceId());
        assertEquals("http://www.opentosca.org/servicetemplates", nodeTemplateInstances.get(0).getServiceTemplateId().getNamespaceURI());
    }

    @Test
    public void testRelationshipTemplateInstances() throws Exception {
        givenYamlOfTestStackResource();

        whenServiceTemplateInstanceCreated();

        thenAssertRelationshipTemplateInstances();
    }

    private void givenYamlOfTestStackResource() {
        this.resource = new ClassPathResource("deployments/unit-tests/teststackmore_EDiMM.yaml");
        this.yamlParser = new YamlParser();
    }

    private void thenAssertRelationshipTemplateInstances() {
        RelationshipTemplateInstance relationshipTemplateInstance = this.serviceTemplateInstance.getRelationshipTemplateInstances().get(0);

        assertNotNull(relationshipTemplateInstance);
        assertEquals("DependsOn", relationshipTemplateInstance.getRelationshipType().getLocalPart());
        assertEquals("4aeb6497-90e8-4c68-99b4-dea09d4848da", relationshipTemplateInstance.getTargetNodeTemplateInstanceId());
        assertEquals(TOSCAState.RelationshipTemplateInstanceState.CREATED, relationshipTemplateInstance.getState());
        assertEquals("109d58f4-9489-4eb6-b1c7-fc00a171c6e4", relationshipTemplateInstance.getSourceNodeTemplateInstanceId());
        assertNotNull(relationshipTemplateInstance.getServiceTemplateInstanceId());
        assertTrue(relationshipTemplateInstance.getInstanceProperties().isEmpty());
    }

    @Test
    public void testTOSCAProperties() throws Exception {
        givenYamlOfTestStackResource();

        whenServiceTemplateInstanceCreated();

        thenAssertTOSCAProperties();
    }

    private void thenAssertTOSCAProperties() {
        List<NodeTemplateInstance> nodeTemplateInstances = this.serviceTemplateInstance.getNodeTemplateInstances();
        List<TOSCAProperty> toscaProperties = nodeTemplateInstances.get(1).getInstanceProperties();

        assertNotNull(toscaProperties);
        assertEquals(8, toscaProperties.size());
        assertEquals("admin_pass", toscaProperties.get(0).getName());
        assertEquals("test_server", toscaProperties.get(1).getValue());
        assertNotNull(toscaProperties.get(2).getName());
    }
}
