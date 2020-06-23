package io.github.edmm.plugins.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.Auth0;
import io.github.edmm.model.component.AwsAurora;
import io.github.edmm.model.component.AwsBeanstalk;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.MysqlDatabase;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.Saas;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.support.EdmmYamlBuilder;
import io.github.edmm.plugins.ansible.AnsiblePlugin;
import io.github.edmm.plugins.cfn.CloudFormationPlugin;
import io.github.edmm.plugins.cfn.rules.AuroraRule;
import io.github.edmm.plugins.cfn.rules.BeanstalkRule;
import io.github.edmm.plugins.cfn.rules.CfnPaasRule;
import org.junit.Assert;
import org.junit.Test;

public class RulesTests {

    @Test
    public void testRuleAssessorWithSameModel() {
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        yamlBuilder
            .component(WebApplication.class)
            .dependsOn(Saas.class)
            .dependsOn(Auth0.class)
            .component(Saas.class)
            .component(Auth0.class);

        DeploymentModel deploymentModel = DeploymentModel.of(yamlBuilder.build());
        Optional<RootComponent> currentComponent = deploymentModel.getComponent("WebApplication");
        RuleAssessor ruleAssessor = new RuleAssessor();

        if (currentComponent.isPresent())
            Assert.assertTrue(ruleAssessor.assess(deploymentModel, deploymentModel, currentComponent.get(),false).matches());
        else
            Assert.fail("component not present");
    }

    @Test
    public void testRuleAssessorSwappingModels() {
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

        Optional<RootComponent> currentComponent = actualModel.getComponent("WebApplication");
        RuleAssessor ruleAssessor  = new RuleAssessor();

        // the actual model should match the expected model, but not the other way around
        if (currentComponent.isPresent())
            Assert.assertTrue(ruleAssessor.assess(expectedModel,actualModel,currentComponent.get(),false).matches());
        else Assert.fail("component not present");

        currentComponent = expectedModel.getComponent("WebApplication");
        ruleAssessor  = new RuleAssessor();

        if (currentComponent.isPresent())
            Assert.assertFalse(ruleAssessor.assess(actualModel, expectedModel,currentComponent.get(),false).matches());
        else Assert.fail("component not present");
    }

    @Test
    public void testRuleAssessorWithWebApplication() {
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        yamlBuilder
            .component(WebApplication.class)
            .hostedOn(WebServer.class)
            .component(WebServer.class)
            .hostedOn(Compute.class)
            .component(Compute.class)
            .build();

        DeploymentModel model = DeploymentModel.of(yamlBuilder.build());
        Optional<RootComponent> currentComponent = model.getComponent("WebServer");
        RuleAssessor ruleAssessor = new RuleAssessor();

        if (currentComponent.isPresent())
            Assert.assertTrue(ruleAssessor.assess(model,model,currentComponent.get(),false).matches());
        else
            Assert.fail("component not present");
    }

    @Test
    public void testRuleEngineWithAnsible(){
        RuleEngine ruleEngine = new RuleEngine();
        AnsiblePlugin ansible = new AnsiblePlugin();
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        // pet-clinic like topology
        yamlBuilder.component(Auth0.class)
                   .component(WebApplication.class)
                   .hostedOn(AwsBeanstalk.class)
                   .connectsTo(Auth0.class)
                   .connectsTo(MysqlDatabase.class)
                   .component(MysqlDatabase.class)
                   .hostedOn(AwsAurora.class)
                   .component(AwsAurora.class)
                   .component(AwsBeanstalk.class);

        DeploymentModel model = DeploymentModel.of(yamlBuilder.build());
        TransformationContext context = new TransformationContext(model, ansible.getDeploymentTechnology());

        List<Rule.Result> results = ruleEngine.fire(context, ansible);

        Assert.assertEquals(3,results.size());

        for (Rule.Result result: results) {
            List<String> unsupportedComponents = result.getUnsupportedComponents();
            boolean contains = unsupportedComponents.contains("AwsAurora") ||
                               unsupportedComponents.contains("AwsBeanstalk") ||
                               unsupportedComponents.contains("Auth0");
            Assert.assertTrue(contains);
        }
    }

    @Test
    public void testRuleExceptionWithCfnPaasRule(){
        CfnPaasRule cfnPaasRule = new CfnPaasRule();

        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        yamlBuilder
            .component(AwsBeanstalk.class);
        DeploymentModel actualModel = DeploymentModel.of(yamlBuilder.build());

        Optional<RootComponent> component = actualModel.getComponent("AwsBeanstalk");

        // AwsBeanstalk is in the exception list so the evaluation should return false
        if (component.isPresent())
            Assert.assertFalse(cfnPaasRule.evaluate(actualModel,  component.get()));
        else
            Assert.fail("component not present");

        yamlBuilder = new EdmmYamlBuilder();
        yamlBuilder
            .component(Paas.class);
        actualModel = DeploymentModel.of(yamlBuilder.build());
        component = actualModel.getComponent("Paas");

        // every other Paas component evaluation should return true instead
        if (component.isPresent())
            Assert.assertTrue(cfnPaasRule.evaluate(actualModel,  component.get()));
        else
            Assert.fail("component not present");
    }

    @Test
    public void testRuleEngineWithCfnRules() {
        CloudFormationPlugin cfnPlugin = new CloudFormationPlugin();
        RuleEngine ruleEngine = new RuleEngine();

        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        yamlBuilder
            .component(WebServer.class, "WebServer1")
            .hostedOn(Compute.class,"Compute1")
            .component(Compute.class,"Compute1");
        DeploymentModel actualModel = DeploymentModel.of(yamlBuilder.build());
        Optional<RootComponent> currentComponent = actualModel.getComponent("WebServer1");

        if (currentComponent.isPresent()) {
            List<Rule> rules = cfnPlugin.getRules();
            // we add the same rule another time, it will be executed but at the end we don't want the same result two times
            rules.add(new BeanstalkRule());

            List<Rule.Result> results = ruleEngine.fire(actualModel, rules, currentComponent.get());

            Assert.assertEquals(1,results.size());
            List<String> unsupportedComponents = results.get(0).getUnsupportedComponents();
            Assert.assertTrue(unsupportedComponents.contains("WebServer1") && unsupportedComponents.contains("Compute1"));
        } else
            Assert.fail("component not present");
    }

    @Test
    public void testPriority() {
        RuleEngine ruleEngine = new RuleEngine();
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        yamlBuilder.component(Saas.class)
                   .hostedOn(Paas.class)
                   .component(Paas.class);
        DeploymentModel actualModel = DeploymentModel.of(yamlBuilder.build());

        List<Rule> rules = new ArrayList<>();
        rules.add(new SaasDefaultRule());
        rules.add(new CfnPaasRule());
        rules.add(new AuroraRule());



        for (RootComponent component: actualModel.getComponents()) {
             ruleEngine.fire(actualModel, rules, component);
        }
        // bad trick to get the results list
        List<Rule.Result> results  = ruleEngine.fire(actualModel, new ArrayList<>(), null);
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("Paas", results.get(0).getUnsupportedComponents().get(0));
        Assert.assertEquals("Saas", results.get(1).getUnsupportedComponents().get(0));
    }
}
