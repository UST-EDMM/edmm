package io.github.edmm.plugins.terraform;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Platform;
import io.github.edmm.model.visitor.VisitorHelper;
import io.github.edmm.plugins.terraform.aws.TerraformAwsVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerraformLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(TerraformLifecycle.class);

    public TerraformLifecycle(TransformationContext context) {
        super(context);
    }

    @Override
    public CheckModelResult checkModel() {
        TerraformSupportVisitor visitor = new TerraformSupportVisitor(context);
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        return visitor.getResult();
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Terraform...");
        TerraformVisitor visitor = new TerraformAwsVisitor(context);
        // Visit compute components first
        VisitorHelper.visit(context.getModel().getComponents(), visitor, component -> component instanceof Compute || component instanceof Platform);
        // ... then all others
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        VisitorHelper.visit(context.getModel().getRelations(), visitor);
        visitor.populateTerraformFile();
        logger.info("Transformation to Terraform successful");
    }
}
