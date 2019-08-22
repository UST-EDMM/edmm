package io.github.edmm.plugins.terraform;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.visitor.VisitorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerraformLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(TerraformLifecycle.class);

    public static final String FILE_NAME = "deploy.tf";

    private final TransformationContext context;

    public TerraformLifecycle(TransformationContext context) {
        this.context = context;
    }

    @Override
    public void prepare() {
        logger.info("Prepare transformation for Terraform...");
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Terraform...");
        TerraformVisitor visitor = new TerraformAwsVisitor(context);
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        visitor.populateTerraformFile();
        logger.info("Transformation to Terraform successful");
    }

    @Override
    public void cleanup() {
        logger.info("Cleanup transformation leftovers...");
        // noop
    }
}
