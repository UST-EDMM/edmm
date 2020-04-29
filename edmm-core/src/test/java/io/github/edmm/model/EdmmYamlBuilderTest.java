package io.github.edmm.model;

import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.support.EdmmYamlBuilder;
import org.junit.Assert;
import org.junit.Test;

public class EdmmYamlBuilderTest {

    @Test
    public void testSimpleYaml(){
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();

        String yamlString =  yamlBuilder.component(Paas.class, Compute.class).buildToYamlString();

        try {
            DeploymentModel.of(yamlString);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
