package io.github.ust.edmm.model;

import io.github.ust.edmm.model.component.Compute;
import io.github.ust.edmm.model.component.SoftwareComponent;
import io.github.ust.edmm.model.relation.HostedOn;
import io.github.ust.edmm.model.relation.RootRelation;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class DeploymentModelTest {

    @Test
    public void testResolveProperties() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/simple_props_only.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Compute ubuntu = (Compute) model.getComponent("ubuntu").orElseThrow(IllegalStateException::new);
        Assert.assertEquals("Ubuntu node", ubuntu.getDescription().orElse(null));
        Assert.assertEquals(1, ubuntu.getMetadata().size());
        Assert.assertEquals(3, ubuntu.getProperties().size());
        Assert.assertEquals("22", ubuntu.getProperties().get("port").getValue());
        Assert.assertEquals("test", ubuntu.getProperties().get("os_family").getValue());
        Assert.assertEquals("test", ubuntu.getOsFamily().orElse(null));
        Assert.assertNull(ubuntu.getProperties().get("machine_image").getValue());
        Assert.assertNull(ubuntu.getMachineImage().orElse(null));
    }

    @Test
    public void testResolveOperations() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/simple_ops_only.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Compute ubuntu = (Compute) model.getComponent("ubuntu").orElseThrow(IllegalStateException::new);
        Assert.assertEquals(3, ubuntu.getOperations().size());
        Operation configureOperation = ubuntu.getOperations().get("configure");
        Assert.assertEquals(1, configureOperation.getArtifacts().size());
        Assert.assertEquals("script", configureOperation.getArtifacts().get(0).getName());
        Assert.assertEquals("configure.sh", configureOperation.getArtifacts().get(0).getValue());
    }

    @Test
    public void testResolveArtifacts() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/simple_ops_only.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Compute ubuntu = (Compute) model.getComponent("ubuntu").orElseThrow(IllegalStateException::new);
        Assert.assertEquals(2, ubuntu.getArtifacts().size());
        Assert.assertEquals("iso", ubuntu.getArtifacts().get(0).getName());
        Assert.assertEquals("ubuntu.iso", ubuntu.getArtifacts().get(0).getValue());
        Assert.assertEquals("war", ubuntu.getArtifacts().get(1).getName());
        Assert.assertEquals("app.war", ubuntu.getArtifacts().get(1).getValue());
    }

    @Test
    public void testResolveRelations() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/simple_relations.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        SoftwareComponent tomcat = (SoftwareComponent) model.getComponent("tomcat").orElseThrow(IllegalStateException::new);
        Assert.assertEquals(2, tomcat.getRelations().size());
        Assert.assertEquals("hosted_on", tomcat.getRelations().get(0).getName());
        Assert.assertEquals("depends_on", tomcat.getRelations().get(1).getName());
        RootRelation hostedOn = tomcat.getRelations().get(0);
        Assert.assertTrue(hostedOn instanceof HostedOn);
        Assert.assertEquals("ubuntu", hostedOn.getTarget());
        Assert.assertEquals(0, hostedOn.getProperties().size());
        Assert.assertEquals(6, hostedOn.getOperations().size());
        Assert.assertEquals(2, model.getTopology().vertexSet().size());
        Assert.assertEquals(2, model.getTopology().edgeSet().size());
    }
}
