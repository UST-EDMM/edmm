package io.github.edmm.plugins.salt;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.visitor.VisitorHelper;
import io.github.edmm.plugins.ComputeSupportVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaltStackLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(SaltStackLifecycle.class);

    public SaltStackLifecycle(TransformationContext context) {
        super(context);
    }

    @Override
    public CheckModelResult checkModel() {
        ComputeSupportVisitor visitor = new ComputeSupportVisitor(context);
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        return visitor.getResult();
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Salt...");
        SaltStackTransformer visitor = new SaltStackTransformer(context);
        // Visit compute components first
        visitor.visitComponentsTopologicalOrder();
        visitor.populateSaltFiles();
        logger.info("Transformation to Salt successful");
    }
}
