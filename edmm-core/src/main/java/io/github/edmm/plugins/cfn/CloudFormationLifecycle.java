package io.github.edmm.plugins.cfn;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.visitor.VisitorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudFormationLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(CloudFormationLifecycle.class);

    public static final String FILE_NAME = "deploy.tf";

    private final TransformationContext context;

    public CloudFormationLifecycle(TransformationContext context) {
        this.context = context;
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to AWS CloudFormation...");
        CloudFormationVisitor visitor = new CloudFormationVisitor(context);
        // Visit compute components first
        VisitorHelper.visit(context.getModel().getComponents(), visitor, component -> component instanceof Compute);
        // ... then all others
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        VisitorHelper.visit(context.getModel().getRelations(), visitor);
        visitor.populateFile();
        logger.info("Transformation to AWS CloudFormation successful");
    }
}
