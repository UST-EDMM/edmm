package io.github.edmm.plugins.cfn;

import java.io.IOException;
import java.util.UUID;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.visitor.VisitorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudFormationLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(CloudFormationLifecycle.class);

    private final TransformationContext context;

    private CloudFormationModule module;

    public CloudFormationLifecycle(TransformationContext context) {
        this.context = context;
    }

    @Override
    public void prepare() {
        AWSCredentials credentials = new BasicAWSCredentials("edmm", UUID.randomUUID().toString());
        this.module = new CloudFormationModule(context.getFileAccess(), "eu-west-1", credentials);
        this.module.setKeyPair(false);
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to AWS CloudFormation...");
        CloudFormationVisitor visitor = new CloudFormationVisitor(context, module);
        // Visit compute components first
        VisitorHelper.visit(context.getModel().getComponents(), visitor, component -> component instanceof Compute);
        // ... then all others
        // VisitorHelper.visit(context.getModel().getComponents(), visitor);
        // VisitorHelper.visit(context.getModel().getRelations(), visitor);
        // Write template file
        PluginFileAccess fileAccess = context.getFileAccess();
        try {
            fileAccess.append("template.yaml", module.toString());
        } catch (IOException e) {
            logger.error("Failed to write template file", e);
            throw new TransformationException(e);
        }
        logger.info("Transformation to AWS CloudFormation successful");
    }
}
