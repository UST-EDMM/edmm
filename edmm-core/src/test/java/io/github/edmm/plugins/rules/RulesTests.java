package io.github.edmm.plugins.rules;

import java.util.Optional;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.Auth0;
import io.github.edmm.model.component.AwsBeanstalk;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
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
        String yaml = yamlBuilder.component(AwsBeanstalk.class).build();
        DeploymentModel actualModel = DeploymentModel.of(yaml);
        Optional<RootComponent> unsupportedComponent = actualModel.getComponent("AwsBeanstalk");

        if (unsupportedComponent.isPresent())
            Assert.assertTrue(paasDefaultRule.evaluate(actualModel,  unsupportedComponent.get()));
        else
            Assert.fail("component not present");
    }

    @Test
    public void testRuleAssessor1() {
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        yamlBuilder
            .component(WebApplication.class)
            .dependsOn(Saas.class)
            .dependsOn(Auth0.class)
            .component(Saas.class)
            .component(Auth0.class)
            .build();

        DeploymentModel deploymentModel = DeploymentModel.of(yamlBuilder.build());
        Optional<RootComponent> unsupportedComponent = deploymentModel.getComponent("WebApplication");
        RuleAssessor ruleAssessor = new RuleAssessor(deploymentModel,deploymentModel);

        if (unsupportedComponent.isPresent())
            Assert.assertTrue(ruleAssessor.assess(unsupportedComponent.get()));
        else
            Assert.fail("component not present");
    }

    @Test
    public void testRuleAssessor2() {
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        yamlBuilder
            .component(WebApplication.class)
            .dependsOn(Saas.class)
            .component(Saas.class);
        DeploymentModel expectedModel = DeploymentModel.of(yamlBuilder.build());

        yamlBuilder = new EdmmYamlBuilder()
            .component(WebApplication.class)
            .dependsOn(Auth0.class)
            .component(Auth0.class);
        DeploymentModel actualModel = DeploymentModel.of(yamlBuilder.build());

        Optional<RootComponent> unsupportedComponent = actualModel.getComponent("WebApplication");
        RuleAssessor ruleAssessor  = new RuleAssessor(expectedModel,actualModel);

        if (unsupportedComponent.isPresent())
            Assert.assertTrue(ruleAssessor.assess(unsupportedComponent.get()));
        else Assert.fail("component not present");

        unsupportedComponent = expectedModel.getComponent("WebApplication");
        ruleAssessor  = new RuleAssessor(actualModel, expectedModel);

        if (unsupportedComponent.isPresent())
            Assert.assertFalse(ruleAssessor.assess(unsupportedComponent.get()));
        else Assert.fail("component not present");
    }

    @Test
    public void testRuleAssessor3() {
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        yamlBuilder
            .component(WebApplication.class)
            .hostedOn(WebServer.class)
            .component(WebServer.class)
            .hostedOn(Compute.class)
            .component(Compute.class)
            .build();

        DeploymentModel deploymentModel = DeploymentModel.of(yamlBuilder.build());
        Optional<RootComponent> unsupportedComponent = deploymentModel.getComponent("WebServer");
        RuleAssessor ruleAssessor = new RuleAssessor(deploymentModel,deploymentModel);

        if (unsupportedComponent.isPresent())
            Assert.assertTrue(ruleAssessor.assess(unsupportedComponent.get()));
        else
            Assert.fail("component not present");
    }
}
