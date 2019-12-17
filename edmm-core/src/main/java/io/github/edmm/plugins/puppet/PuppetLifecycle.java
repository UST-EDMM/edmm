package io.github.edmm.plugins.puppet;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.visitor.VisitorHelper;
import io.github.edmm.plugins.ComputeSupportVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuppetLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(PuppetLifecycle.class);

    public static final String MODULE_FILES_FOLDER = "files";
    public static final String MODULE_MANIFESTS_FOLDER = "manifests";
    public static final String MANIFEST_MAIN = "init";
    public static final String MANIFEST_EXTENSION = ".pp";

    public PuppetLifecycle(TransformationContext context) {
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
        logger.info("Begin transformation to Puppet...");
        PuppetTransformer transformer = new PuppetTransformer(context);
        transformer.populateManifest();
        logger.info("Transformation to Puppet successful");
    }
}
