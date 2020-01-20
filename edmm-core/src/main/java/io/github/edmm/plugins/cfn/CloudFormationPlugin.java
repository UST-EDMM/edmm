package io.github.edmm.plugins.cfn;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class CloudFormationPlugin extends Plugin<CloudFormationLifecycle> {

    public static final TargetTechnology CFN = TargetTechnology.builder().id("cfn").name("AWS CloudFormation").build();

    public CloudFormationPlugin() {
        super(CFN);
    }

    @Override
    public CloudFormationLifecycle getLifecycle(TransformationContext context) {
        return new CloudFormationLifecycle(context);
    }
}
