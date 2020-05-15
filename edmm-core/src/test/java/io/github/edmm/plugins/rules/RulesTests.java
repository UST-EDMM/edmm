package io.github.edmm.plugins.rules;

import java.util.List;
import java.util.Map;
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
import io.github.edmm.plugins.cfn.rules.CfnPaasRule;
import org.junit.Assert;
import org.junit.Test;

public class RulesTests {

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
        RuleAssessor ruleAssessor = new RuleAssessor();

        if (unsupportedComponent.isPresent())
            Assert.assertTrue(ruleAssessor.assess(deploymentModel,deploymentModel,unsupportedComponent.get(),false));
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
        RuleAssessor ruleAssessor  = new RuleAssessor();

        if (unsupportedComponent.isPresent())
            Assert.assertTrue(ruleAssessor.assess(expectedModel,actualModel,unsupportedComponent.get(),false));
        else Assert.fail("component not present");

        unsupportedComponent = expectedModel.getComponent("WebApplication");
        ruleAssessor  = new RuleAssessor();

        if (unsupportedComponent.isPresent())
            Assert.assertFalse(ruleAssessor.assess(actualModel, expectedModel,unsupportedComponent.get(),false));
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

        DeploymentModel model = DeploymentModel.of(yamlBuilder.build());
        Optional<RootComponent> unsupportedComponent = model.getComponent("WebServer");
        RuleAssessor ruleAssessor = new RuleAssessor();

        if (unsupportedComponent.isPresent())
            Assert.assertTrue(ruleAssessor.assess(model,model,unsupportedComponent.get(),false));
        else
            Assert.fail("component not present");
    }

    @Test
    public void testRuleEngine(){
        RuleEngine ruleEngine = new RuleEngine();
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder().component(Saas.class);
        DeploymentModel model = DeploymentModel.of(yamlBuilder.build());
        Optional<RootComponent> unsupportedComponent = model.getComponent("Saas");

        if (unsupportedComponent.isPresent()) {
            ruleEngine.fire(model, Rule.getDefault(), unsupportedComponent.get());
            List<Rule.Result> result = ruleEngine.getResults().get(unsupportedComponent.get().getName());
            Assert.assertEquals(1,result.size());

            SaasDefaultRule saasDefaultRule = new SaasDefaultRule();
            Map<String,Object> expected = saasDefaultRule.execute().getToTopology();
            Assert.assertEquals( expected, result.get(0).getToTopology());
        } else
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

        ruleEngine.fire(context, ansible);
        Map<String,List<Rule.Result>> results = ruleEngine.getResults();

        Assert.assertEquals(3,results.keySet().size());
        Assert.assertNotNull(results.get("AwsAurora"));
        Assert.assertNotNull(results.get("AwsBeanstalk"));
        Assert.assertNotNull(results.get("Auth0"));

        DbaasDefaultRule dbaasDefaultRule = new DbaasDefaultRule();
        Assert.assertEquals(dbaasDefaultRule.execute().getToTopology(),
            results.get("AwsAurora").get(0).getToTopology());
    }

    @Test
    public void testRuleExceptionWithCfnPaasRule(){
        CfnPaasRule cfnPaasRule = new CfnPaasRule();

        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();
        yamlBuilder.component(AwsBeanstalk.class);
        DeploymentModel actualModel = DeploymentModel.of(yamlBuilder.build());

        Optional<RootComponent> component = actualModel.getComponent("AwsBeanstalk");

        if (component.isPresent())
            Assert.assertFalse(cfnPaasRule.evaluate(actualModel,  component.get()));
        else
            Assert.fail("component not present");

        yamlBuilder = new EdmmYamlBuilder();
        yamlBuilder.component(Paas.class);
        actualModel = DeploymentModel.of(yamlBuilder.build());;
        component = actualModel.getComponent("Paas");

        if (component.isPresent())
            Assert.assertTrue(cfnPaasRule.evaluate(actualModel,  component.get()));
        else
            Assert.fail("component not present");
    }
}
