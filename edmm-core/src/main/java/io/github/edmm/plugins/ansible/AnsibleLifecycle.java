package io.github.edmm.plugins.ansible;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.visitor.VisitorHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnsibleLifecycle extends AbstractLifecycle {

    public static final String FILE_NAME = "deployment.yml";

    private static final Logger logger = LoggerFactory.getLogger(AnsibleLifecycle.class);

    public AnsibleLifecycle(TransformationContext context) {
        super(context);
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Ansible...");
        AnsibleTransformer visitor = new AnsibleTransformer(context);
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        visitor.populateAnsibleFile();
        logger.info("Transformation to Ansible successful");
    }
}
