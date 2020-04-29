package io.github.edmm.plugins.cloudify;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.visitor.VisitorHelper;
import io.github.edmm.plugins.ComputeSupportVisitor;
import io.github.edmm.plugins.cloudify.azure.CloudifyAzureVisitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudifyLifecycle extends AbstractLifecycle {

    public static final String FILE_NAME = "deploy.yml";
    private static final Logger logger = LoggerFactory.getLogger(CloudifyLifecycle.class);

    public CloudifyLifecycle(TransformationContext context) {
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
        logger.info("Begin transformation to Cloudify Azure...");
        CloudifyAzureVisitor visitor = new CloudifyAzureVisitor(context);
        // Visit compute components first
        VisitorHelper.visit(context.getModel().getComponents(), visitor, component -> component instanceof Compute);
        // ... then all others
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        VisitorHelper.visit(context.getModel().getRelations(), visitor);
        visitor.populateCloudifyFile();
        logger.info("Transformation to Cloudify Azure successful");
    }
}
