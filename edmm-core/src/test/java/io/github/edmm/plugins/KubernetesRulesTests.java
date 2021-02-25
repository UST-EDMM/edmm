package io.github.edmm.plugins;

import java.util.List;

import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.AwsBeanstalk;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.support.EdmmYamlBuilder;
import io.github.edmm.plugins.kubernetes.KubernetesPlugin;
import io.github.edmm.plugins.rules.Rule;
import io.github.edmm.plugins.rules.RuleEngine;

import org.junit.Assert;
import org.junit.Test;

public class KubernetesRulesTests {

    @Test
    public void testBeanstalkRuleRule() {
        RuleEngine engine = new RuleEngine();
        KubernetesPlugin plugin = new KubernetesPlugin();

        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder()
            .component(WebApplication.class, "app")
            .hostedOn(AwsBeanstalk.class)
            .component(AwsBeanstalk.class, "platform");
        DeploymentModel model = DeploymentModel.of(yamlBuilder.build());
        TransformationContext context = new TransformationContext(model, plugin.getDeploymentTechnology());

        List<Rule.Result> results = engine.fire(context, plugin);

        Assert.assertEquals(results.size(), 1);
        Assert.assertEquals(results.get(0).getUnsupportedComponents().size(), 1);
        Assert.assertEquals(results.get(0).getToTopology().size(), 2);
    }
}
