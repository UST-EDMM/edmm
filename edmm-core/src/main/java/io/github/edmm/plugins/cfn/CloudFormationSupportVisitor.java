package io.github.edmm.plugins.cfn;

import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.AwsAurora;
import io.github.edmm.model.component.AwsBeanstalk;
import io.github.edmm.plugins.ComputeSupportVisitor;

public class CloudFormationSupportVisitor extends ComputeSupportVisitor {

    public CloudFormationSupportVisitor(TransformationContext context) {
        super(context);
    }

    @Override
    public void visit(AwsAurora component) {
        supportedComponents.add(component);
    }

    @Override
    public void visit(AwsBeanstalk component) {
        supportedComponents.add(component);
    }
}
