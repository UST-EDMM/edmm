package io.github.ust.edmm.plugins.terraform;

import io.github.ust.edmm.core.plugin.AbstractLifecycle;
import io.github.ust.edmm.core.transformation.TransformationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerraformLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(TerraformLifecycle.class);

    private final TransformationContext context;

    public TerraformLifecycle(TransformationContext context) {
        this.context = context;
    }

    @Override
    public void prepare() {
        logger.info("Prepare transformation for Terraform...");
        // noop
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Terraform...");
        // TODO
        logger.info("Transformation to Terraform successful");
    }

    @Override
    public void cleanup() {
        logger.info("Cleanup transformation leftovers...");
        // noop
    }
}
