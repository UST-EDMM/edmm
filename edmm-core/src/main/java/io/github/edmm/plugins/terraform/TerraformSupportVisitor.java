package io.github.edmm.plugins.terraform;

import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.Auth0;
import io.github.edmm.model.component.AwsAurora;
import io.github.edmm.model.component.AwsBeanstalk;
import io.github.edmm.plugins.ComputeSupportVisitor;

public class TerraformSupportVisitor extends ComputeSupportVisitor {

    public TerraformSupportVisitor(TransformationContext context) {
        super(context);
    }

    @Override
    public void visit(Auth0 component) {
        supportedComponents.add(component);
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
