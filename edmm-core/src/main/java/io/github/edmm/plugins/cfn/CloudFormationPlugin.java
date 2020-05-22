package io.github.edmm.plugins.cfn;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.plugins.cfn.rules.AuroraRule;
import io.github.edmm.plugins.cfn.rules.BeanstalkRule;
import io.github.edmm.plugins.cfn.rules.CfnDbaasRule;
import io.github.edmm.plugins.cfn.rules.CfnPaasRule;
import io.github.edmm.plugins.rules.Rule;
import io.github.edmm.plugins.rules.SaasDefaultRule;

public class CloudFormationPlugin extends TransformationPlugin<CloudFormationLifecycle> {

    public static final DeploymentTechnology CFN = DeploymentTechnology.builder().id("cfn").name("AWS CloudFormation").build();

    public CloudFormationPlugin() {
        super(CFN);
    }

    @Override
    public CloudFormationLifecycle getLifecycle(TransformationContext context) {
        return new CloudFormationLifecycle(context);
    }

    @Override
    public List<Rule> getRules() {
        List<Rule> rules = new ArrayList<>();
        rules.add(new SaasDefaultRule());
        rules.add(new CfnPaasRule());
        rules.add(new CfnDbaasRule());
        rules.add(new BeanstalkRule());
        rules.add(new AuroraRule());
        return rules;
    }
}
