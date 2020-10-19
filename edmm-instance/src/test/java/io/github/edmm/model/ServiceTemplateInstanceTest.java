package io.github.edmm.model;

import java.util.List;

import javax.xml.namespace.QName;

import io.github.edmm.core.transformation.TOSCATransformer;
import io.github.edmm.core.yaml.EDMMiYamlParser;
import io.github.edmm.model.edimm.DeploymentInstance;
import io.github.edmm.model.opentosca.NodeTemplateInstance;
import io.github.edmm.model.opentosca.ServiceTemplateInstance;
import io.github.edmm.model.opentosca.TOSCAState;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

class ServiceTemplateInstanceTest {

    private ClassPathResource resource;
    private EDMMiYamlParser EDMMiYamlParser;
    private ServiceTemplateInstance serviceTemplateInstance;
    private DeploymentInstance deploymentInstance;

    @Test
    public void testServiceTemplate() throws Exception {
        givenYamlOfNginxResource();

        whenServiceTemplateInstanceCreated();

        thenAssertServiceTemplateInstance();
    }

    private void givenYamlOfNginxResource() {
        this.resource = new ClassPathResource("deployments/unit-tests/nginx-deployment_EDMMi.yaml");
        this.EDMMiYamlParser = new EDMMiYamlParser();
    }

    private void whenServiceTemplateInstanceCreated() throws Exception {
        this.deploymentInstance = this.EDMMiYamlParser.parseYamlAndTransformToDeploymentInstance(this.resource.getFile().getAbsolutePath());
        this.serviceTemplateInstance = new TOSCATransformer().transformEDiMMToServiceTemplateInstance(this.deploymentInstance);
    }

    private void thenAssertServiceTemplateInstance() {
        assertNotNull(this.serviceTemplateInstance);
        assertEquals("2020-04-20T11:32:44.000+02:00", this.serviceTemplateInstance.getCreatedAt());
        assertEquals(3, this.serviceTemplateInstance.getNodeTemplateInstances().size());
        assertEquals("nginx-deployment", this.serviceTemplateInstance.getCsarId());
        assertEquals(TOSCAState.ServiceTemplateInstanceState.CREATED, this.serviceTemplateInstance.getState());
        assertEquals(new QName("http://opentosca.org/servicetemplates", "nginx-deployment"), this.serviceTemplateInstance.getServiceTemplateId());
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
        assertEquals(new QName("http://opentosca.org/nodetemplates", "nginx-deployment-6b474476c4-22gtg"), nodeTemplateInstances.get(0).getNodeTemplateId());
        assertEquals(new QName("http://docs.oasis-open.org/tosca/ToscaNormativeTypes", "Compute"), nodeTemplateInstances.get(0).getNodeType());
        assertEquals("a3ec9267-aff0-4484-ad5e-785502c9d2d5", nodeTemplateInstances.get(0).getNodeTemplateInstanceId());
        assertEquals("92d76709-1fca-4d77-9865-c9cbf0dc7106", nodeTemplateInstances.get(0).getServiceTemplateInstanceId());
        assertEquals("http://opentosca.org/servicetemplates", nodeTemplateInstances.get(0).getServiceTemplateId().getNamespaceURI());
    }


    private void givenYamlOfTestStackResource() {
        this.resource = new ClassPathResource("deployments/unit-tests/teststackmore_EDMMi.yaml");
        this.EDMMiYamlParser = new EDMMiYamlParser();
    }

}
