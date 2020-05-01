package io.github.edmm.plugins.rules;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.Auth0;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.Saas;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.support.EdmmYamlBuilder;
import org.junit.Assert;
import org.junit.Test;


public class RulesTests {

    @Test
    public void testEvaluatePaas() {

        PaasDefaultRule paasDefaultRule = new PaasDefaultRule();
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        String yaml = yamlBuilder.component(Paas.class).build();
        DeploymentModel actualModel = DeploymentModel.of(yaml);
        Assert.assertTrue(paasDefaultRule.evaluate(actualModel, actualModel.getTopology()));
    }

    @Test
    public void testEvaluateWebServer() {

        PaasDefaultRule paasDefaultRule = new PaasDefaultRule();
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        String yaml = yamlBuilder.component(WebServer.class).build();
        DeploymentModel actualModel = DeploymentModel.of(yaml);
        Assert.assertFalse(paasDefaultRule.evaluate(actualModel, actualModel.getTopology()));
    }

    @Test
    public void testEvaluateStubRule() {

        CustomRule customRule = new CustomRule();
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        String yaml = yamlBuilder
            .component(WebApplication.class)
            .dependsOn(Saas.class)
            .dependsOn(Auth0.class)
            .component(Saas.class)
            .component(Auth0.class)
            .build();

        DeploymentModel actualModel = DeploymentModel.of(yaml);
        Assert.assertTrue(customRule.evaluate(actualModel, actualModel.getTopology()));
    }
}
