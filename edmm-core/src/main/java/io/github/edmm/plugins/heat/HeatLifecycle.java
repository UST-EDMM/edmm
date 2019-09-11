package io.github.edmm.plugins.heat;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.visitor.VisitorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeatLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(HeatLifecycle.class);

    private final TransformationContext context;

    public HeatLifecycle(TransformationContext context) {
        this.context = context;
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Heat Orchestration Template...");
        HeatVisitor visitor = new HeatVisitor(context);
        // Visit compute components first
        VisitorHelper.visit(context.getModel().getComponents(), visitor, component -> component instanceof Compute);
        // ... then all others
        VisitorHelper.visit(context.getModel().getRelations(), visitor);
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        visitor.populateHeatTemplate();
        logger.info("Transformation to Heat Orchestration Template successful");
    }
}
