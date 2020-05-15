package io.github.edmm.model;

import java.util.Optional;

import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.support.EdmmYamlBuilder;
import org.junit.Assert;
import org.junit.Test;

public class EdmmYamlBuilderTest {

    @Test
    public void testSimpleYaml(){
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();

        String yamlString =  yamlBuilder.component(Paas.class)
            .hostedOn(Compute.class)
            .component(Compute.class)
            .build();

        try {
            DeploymentModel.of(yamlString);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testSameComponent(){
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();

        String yamlString =  yamlBuilder.component(Paas.class, "PaasOne")
            .hostedOn(Compute.class, "ComputeOne")
            .component(Compute.class, "ComputeOne")
            .component(Compute.class, "ComputeTwo")
            .build();

        DeploymentModel model = DeploymentModel.of(yamlString);
        Optional<RootComponent> computeOne = model.getComponent("ComputeOne");
        Optional<RootComponent> computeTwo = model.getComponent("ComputeTwo");

        Assert.assertTrue(computeOne.isPresent());
        Assert.assertTrue(computeTwo.isPresent());
    }
}
