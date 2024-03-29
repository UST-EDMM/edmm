package io.github.edmm.model;

import java.util.Optional;

import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.support.EdmmYamlBuilder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class EdmmYamlBuilderTest {

    @Test
    public void testSameComponent() {
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();

        String yamlString = yamlBuilder.component(Paas.class, "PaasOne")
            .hostedOn(Compute.class, "ComputeOne")
            .component(Compute.class, "ComputeOne")
            .component(Compute.class, "ComputeTwo")
            .build();

        DeploymentModel model;
        try {
            model = DeploymentModel.of(yamlString);
        } catch (Exception e) {
            fail();
            return;
        }

        Optional<RootComponent> computeOne = model.getComponent("ComputeOne");
        Optional<RootComponent> computeTwo = model.getComponent("ComputeTwo");

        assertTrue(computeOne.isPresent());
        assertTrue(computeTwo.isPresent());
    }
}
