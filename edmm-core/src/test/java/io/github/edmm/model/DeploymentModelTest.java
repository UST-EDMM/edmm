package io.github.edmm.model;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DeploymentModelTest {

    @Test
    public void testResolveProperties() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/properties.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Compute ubuntu = (Compute) model.getComponent("ubuntu").orElseThrow(IllegalStateException::new);
        assertEquals("ubuntu", ubuntu.getDescription().orElse(null));
        assertEquals(6, ubuntu.getProperties().size());
        assertFalse(ubuntu.getProperty("port").isPresent());
        assertTrue(ubuntu.getProperty("os_family").isPresent());
        assertTrue(ubuntu.getProperty("machine_image").isPresent());
        assertTrue(ubuntu.getProperty("instance_type").isPresent());
        assertTrue(ubuntu.getProperty("key_name").isPresent());
        assertTrue(ubuntu.getProperty("public_key").isPresent());
        assertTrue(ubuntu.getProperty("test").isPresent());
        assertEquals("linux", ubuntu.getProperties().get("os_family").getValue());
        assertNull(ubuntu.getProperties().get("key_name").getValue());
        assertEquals("ubuntu", ubuntu.getProperties().get("machine_image").getValue());
        assertEquals("large", ubuntu.getProperties().get("instance_type").getValue());
        assertEquals("string", ubuntu.getProperties().get("test").getType());
        assertEquals("test", ubuntu.getProperties().get("test").getDescription().orElse(null));
        assertEquals("compute", ubuntu.getProperties().get("test").getDefaultValue());
        assertEquals("ubuntu", ubuntu.getProperties().get("test").getValue());
    }

    @Test
    public void testResolveOperations() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/operations.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Compute ubuntu = (Compute) model.getComponent("ubuntu").orElseThrow(IllegalStateException::new);
        assertEquals(5, ubuntu.getOperations().size());
        RootComponent.StandardLifecycle lifecycle = ubuntu.getStandardLifecycle();
        Operation configureOperation = lifecycle.getConfigure().orElseThrow(IllegalStateException::new);
        assertEquals(2, configureOperation.getArtifacts().size());
        assertEquals("script", configureOperation.getArtifacts().get(0).getName());
        assertEquals("configure.sh", configureOperation.getArtifacts().get(0).getValue());
        assertEquals("cmd", configureOperation.getArtifacts().get(1).getName());
        assertEquals("test.sh", configureOperation.getArtifacts().get(1).getValue());
        Operation startOperation = lifecycle.getStart().orElseThrow(IllegalStateException::new);
        assertEquals("cmd", startOperation.getArtifacts().get(0).getName());
        assertEquals("start.sh", startOperation.getArtifacts().get(0).getValue());
        Operation deleteOperation = lifecycle.getDelete().orElseThrow(IllegalStateException::new);
        assertEquals("cmd", deleteOperation.getArtifacts().get(0).getName());
        assertEquals("delete.sh", deleteOperation.getArtifacts().get(0).getValue());
        Operation stopOperation = lifecycle.getStop().orElseThrow(IllegalStateException::new);
        assertEquals(1, stopOperation.getArtifacts().size());
        Operation createOperation = lifecycle.getCreate().orElseThrow(IllegalStateException::new);
        assertEquals(0, createOperation.getArtifacts().size());
    }

    @Test
    public void testResolveArtifacts() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/operations.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Compute ubuntu = (Compute) model.getComponent("ubuntu").orElseThrow(IllegalStateException::new);
        assertEquals(3, ubuntu.getArtifacts().size());
        assertEquals("test", ubuntu.getArtifacts().get(0).getName());
        assertEquals("test.sh", ubuntu.getArtifacts().get(0).getValue());
        assertEquals("iso", ubuntu.getArtifacts().get(1).getName());
        assertEquals("ubuntu.iso", ubuntu.getArtifacts().get(1).getValue());
        assertEquals("war", ubuntu.getArtifacts().get(2).getName());
        assertEquals("app.war", ubuntu.getArtifacts().get(2).getValue());
    }

    @Test
    public void testResolveRelations() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/relations.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        SoftwareComponent tomcat = (SoftwareComponent) model.getComponent("tomcat").orElseThrow(IllegalStateException::new);
        assertEquals(2, tomcat.getRelations().size());
        assertEquals("hosted_on", tomcat.getRelations().get(0).getName());
        assertEquals("depends_on", tomcat.getRelations().get(1).getName());
        RootRelation hostedOn = tomcat.getRelations().get(0);
        assertTrue(hostedOn instanceof HostedOn);
        assertEquals("ubuntu", hostedOn.getTarget());
        assertEquals(0, hostedOn.getProperties().size());
        assertEquals(6, hostedOn.getOperations().size());
        assertEquals(2, model.getTopology().vertexSet().size());
        assertEquals(2, model.getTopology().edgeSet().size());
    }

    @Test
    public void testGenerateYaml() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/relations.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        StringWriter yaml = new StringWriter();
        model.getGraph().generateYamlOutput(yaml);
        ClassPathResource expectedResource = new ClassPathResource("templates/unit-tests/relations_generated.yml");
        String expectedString = FileUtils.readFileToString(expectedResource.getFile(), StandardCharsets.UTF_8);
        assertEquals(expectedString, yaml.toString());
    }
}
