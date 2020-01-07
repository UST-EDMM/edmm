package io.github.edmm.plugins.cfengine;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.visitor.VisitorHelper;
import io.github.edmm.plugins.ComputeSupportVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CFEngineLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(CFEngineLifecycle.class);

    public CFEngineLifecycle(TransformationContext context) {
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
        logger.info("Begin transformation to CFEngine...");
        CFEngineTransformer visitor = new CFEngineTransformer(context);
        // Visit compute components first
        visitor.visitComponentsTopologicalOrder();
        visitor.populateCFEngineFiles();
        logger.info("Transformation to CFEngine successful");
    }
}
