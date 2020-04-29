package io.github.edmm.plugins.rules;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.support.EdmmYamlBuilder;
import org.junit.Assert;
import org.junit.Test;

public class RulesTests {

    @Test
    public void testEvaluate() {

        PaasDefaultRule paasDefaultRule = new PaasDefaultRule();
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        String yaml = yamlBuilder.component(Paas.class).build();
        DeploymentModel actualModel = DeploymentModel.of(yaml);
        Assert.assertTrue(paasDefaultRule.evaluate(actualModel, actualModel.getTopology()));
    }
}
