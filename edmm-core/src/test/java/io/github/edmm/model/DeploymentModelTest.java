package io.github.edmm.model;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import io.github.edmm.model.component.Compute;
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
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/simple_props_only.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Compute ubuntu = (Compute) model.getComponent("ubuntu").orElseThrow(IllegalStateException::new);
        assertEquals("Ubuntu node", ubuntu.getDescription().orElse(null));
        assertEquals(1, ubuntu.getMetadata().size());
        assertEquals(3, ubuntu.getProperties().size());
        assertEquals("22", ubuntu.getProperties().get("port").getValue());
        assertEquals("test", ubuntu.getProperties().get("os_family").getValue());
        assertEquals("test", ubuntu.getOsFamily().orElse(null));
        Assert.assertNull(ubuntu.getProperties().get("machine_image").getValue());
        Assert.assertNull(ubuntu.getMachineImage().orElse(null));
    }

    @Test
    public void testResolveOperations() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/simple_ops_only.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Compute ubuntu = (Compute) model.getComponent("ubuntu").orElseThrow(IllegalStateException::new);
        assertEquals(4, ubuntu.getOperations().size());
        Operation configureOperation = ubuntu.getOperations().get("configure");
        assertEquals(1, configureOperation.getArtifacts().size());
        assertEquals("script", configureOperation.getArtifacts().get(0).getName());
        assertEquals("configure.sh", configureOperation.getArtifacts().get(0).getValue());
        assertEquals("delete.sh", ubuntu.getOperations().get("delete").getArtifacts().get(0).getValue());
        assertEquals("create.sh", ubuntu.getOperations().get("create").getArtifacts().get(0).getValue());
    }

    @Test
    public void testResolveArtifacts() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/simple_ops_only.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Compute ubuntu = (Compute) model.getComponent("ubuntu").orElseThrow(IllegalStateException::new);
        assertEquals(2, ubuntu.getArtifacts().size());
        assertEquals("iso", ubuntu.getArtifacts().get(0).getName());
        assertEquals("ubuntu.iso", ubuntu.getArtifacts().get(0).getValue());
        assertEquals("war", ubuntu.getArtifacts().get(1).getName());
        assertEquals("app.war", ubuntu.getArtifacts().get(1).getValue());
    }

    @Test
    public void testResolveRelations() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/simple_relations.yml");
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
