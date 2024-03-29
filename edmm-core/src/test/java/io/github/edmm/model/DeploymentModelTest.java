package io.github.edmm.model;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.relation.DependsOn;
import io.github.edmm.model.relation.RootRelation;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        List<Artifact> artifacts = ubuntu.getArtifacts();
        assertEquals(3, artifacts.size());

        assertTrue(artifacts.removeIf(artifact ->
            artifact.getName().equals("test") && artifact.getValue().equals("test.sh")
        ));
        assertTrue(artifacts.removeIf(artifact ->
            artifact.getName().equals("iso") && artifact.getValue().equals("ubuntu.iso")
        ));
        assertTrue(artifacts.removeIf(artifact ->
            artifact.getName().equals("war") && artifact.getValue().equals("app.war")
        ));
    }

    @Test
    public void testResolveRelations() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/relations.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        SoftwareComponent tomcat = (SoftwareComponent) model.getComponent("tomcat").orElseThrow(IllegalStateException::new);

        List<RootRelation> relations = tomcat.getRelations();
        assertEquals(3, tomcat.getRelations().size());
        assertTrue(relations.removeIf(artifact -> artifact.getName().equals("depends_on")));
        assertTrue(relations.removeIf(artifact -> artifact.getName().equals("hosted_on")));
        assertEquals(0, relations.size());

        RootRelation relation = tomcat.getRelations().get(2);
        assertTrue(relation instanceof DependsOn);
        assertEquals("db", relation.getTarget());
        assertEquals(0, relation.getProperties().size());
        assertEquals(1, relation.getOperations().size());
        assertEquals(3, model.getTopology().vertexSet().size());
        assertEquals(4, model.getTopology().edgeSet().size());
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
