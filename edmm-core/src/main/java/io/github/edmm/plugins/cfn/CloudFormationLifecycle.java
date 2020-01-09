package io.github.edmm.plugins.cfn;

import java.io.IOException;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.visitor.VisitorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudFormationLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(CloudFormationLifecycle.class);

    private final CloudFormationModule module;

    public CloudFormationLifecycle(TransformationContext context) {
        super(context);
        this.module = new CloudFormationModule("eu-west-1");
        this.module.setKeyPair(true);
    }

    @Override
    public CheckModelResult checkModel() {
        CloudFormationSupportVisitor visitor = new CloudFormationSupportVisitor(context);
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        return visitor.getResult();
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to AWS CloudFormation...");
        CloudFormationVisitor visitor = new CloudFormationVisitor(context, module);
        // Visit compute components first
        VisitorHelper.visit(context.getModel().getComponents(), visitor, component -> component instanceof Compute);
        // ... then all relations
        VisitorHelper.visit(context.getModel().getRelations(), visitor);
        // ... finally all other components
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        // Finalize transformation
        visitor.complete();
        try {
            // Write template file
            context.getFileAccess().append("template.yaml", module.toString());
        } catch (IOException e) {
            logger.error("Failed to write template file", e);
            throw new TransformationException(e);
        }
        logger.info("Transformation to AWS CloudFormation successful");
    }
}
