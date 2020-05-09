package io.github.edmm.plugins.cfn;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.TransformationContext;

public class CloudFormationPlugin extends TransformationPlugin<CloudFormationLifecycle> {

    public static final DeploymentTechnology CFN = DeploymentTechnology.builder().id("cfn").name("AWS CloudFormation").build();

    public CloudFormationPlugin() {
        super(CFN);
    }

    @Override
    public CloudFormationLifecycle getLifecycle(TransformationContext context) {
        return new CloudFormationLifecycle(context);
    }
}
