package io.github.edmm.model;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;

public class DeploymentModelTest {

    @Test
    public void testResolveProperties() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/properties.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Compute ubuntu = (Compute) model.getComponent("ubuntu").orElseThrow(IllegalStateException::new);
        Assert.assertEquals("ubuntu", ubuntu.getDescription().orElse(null));
        Assert.assertEquals(6, ubuntu.getProperties().size());
        Assert.assertFalse(ubuntu.getProperty("port").isPresent());
        Assert.assertTrue(ubuntu.getProperty("os_family").isPresent());
        Assert.assertTrue(ubuntu.getProperty("machine_image").isPresent());
        Assert.assertTrue(ubuntu.getProperty("instance_type").isPresent());
        Assert.assertTrue(ubuntu.getProperty("key_name").isPresent());
        Assert.assertTrue(ubuntu.getProperty("public_key").isPresent());
        Assert.assertTrue(ubuntu.getProperty("test").isPresent());
        Assert.assertEquals("linux", ubuntu.getProperties().get("os_family").getValue());
        Assert.assertNull(ubuntu.getProperties().get("key_name").getValue());
        Assert.assertEquals("ubuntu", ubuntu.getProperties().get("machine_image").getValue());
        Assert.assertEquals("large", ubuntu.getProperties().get("instance_type").getValue());
        Assert.assertEquals("string", ubuntu.getProperties().get("test").getType());
        Assert.assertEquals("test", ubuntu.getProperties().get("test").getDescription().orElse(null));
        Assert.assertEquals("compute", ubuntu.getProperties().get("test").getDefaultValue());
        Assert.assertEquals("ubuntu", ubuntu.getProperties().get("test").getValue());
    }

    @Test
    public void testResolveOperations() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/operations.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Compute ubuntu = (Compute) model.getComponent("ubuntu").orElseThrow(IllegalStateException::new);
        Assert.assertEquals(5, ubuntu.getOperations().size());
        RootComponent.StandardLifecycle lifecycle = ubuntu.getStandardLifecycle();
        Operation configureOperation = lifecycle.getConfigure().orElseThrow(IllegalStateException::new);
        Assert.assertEquals(2, configureOperation.getArtifacts().size());
        Assert.assertEquals("script", configureOperation.getArtifacts().get(0).getName());
        Assert.assertEquals("configure.sh", configureOperation.getArtifacts().get(0).getValue());
        Assert.assertEquals("cmd", configureOperation.getArtifacts().get(1).getName());
        Assert.assertEquals("test.sh", configureOperation.getArtifacts().get(1).getValue());
        Operation startOperation = lifecycle.getStart().orElseThrow(IllegalStateException::new);
        Assert.assertEquals("cmd", startOperation.getArtifacts().get(0).getName());
        Assert.assertEquals("start.sh", startOperation.getArtifacts().get(0).getValue());
        Operation deleteOperation = lifecycle.getDelete().orElseThrow(IllegalStateException::new);
        Assert.assertEquals("cmd", deleteOperation.getArtifacts().get(0).getName());
        Assert.assertEquals("delete.sh", deleteOperation.getArtifacts().get(0).getValue());
        Operation stopOperation = lifecycle.getStop().orElseThrow(IllegalStateException::new);
        Assert.assertEquals(1, stopOperation.getArtifacts().size());
        Operation createOperation = lifecycle.getCreate().orElseThrow(IllegalStateException::new);
        Assert.assertEquals(0, createOperation.getArtifacts().size());
    }

    @Test
    public void testResolveArtifacts() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/operations.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Compute ubuntu = (Compute) model.getComponent("ubuntu").orElseThrow(IllegalStateException::new);
        Assert.assertEquals(3, ubuntu.getArtifacts().size());
        Assert.assertEquals("test", ubuntu.getArtifacts().get(0).getName());
        Assert.assertEquals("test.sh", ubuntu.getArtifacts().get(0).getValue());
        Assert.assertEquals("iso", ubuntu.getArtifacts().get(1).getName());
        Assert.assertEquals("ubuntu.iso", ubuntu.getArtifacts().get(1).getValue());
        Assert.assertEquals("war", ubuntu.getArtifacts().get(2).getName());
        Assert.assertEquals("app.war", ubuntu.getArtifacts().get(2).getValue());
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
        Assert.assertTrue(hostedOn instanceof HostedOn);
        assertEquals("ubuntu", hostedOn.getTarget());
        assertEquals(0, hostedOn.getProperties().size());
        assertEquals(6, hostedOn.getOperations().size());
        assertEquals(2, model.getTopology().vertexSet().size());
        assertEquals(2, model.getTopology().edgeSet().size());
    }

    @Test
    public void testGenerateYaml() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/simple_relations.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        StringWriter stringWriter = new StringWriter();
        model.getGraph().generateYamlOutput(stringWriter);

        String expectedString = new String(Files.readAllBytes(
                new ClassPathResource("templates/unit-tests/simple_relations_generated.yml").getFile().toPath()),
                StandardCharsets.UTF_8);

        assertEquals(expectedString, stringWriter.toString());
    }
}
