package io.github.edmm.plugins.puppet;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuppetLifecycle extends AbstractLifecycle {

    public static final String MODULE_FILES_FOLDER = "files";
    public static final String MODULE_MANIFESTS_FOLDER = "manifests";
    public static final String MANIFEST_MAIN = "init";
    public static final String MANIFEST_EXTENSION = ".pp";

    private static final Logger logger = LoggerFactory.getLogger(PuppetLifecycle.class);

    public PuppetLifecycle(TransformationContext context) {
        super(context);
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Puppet...");
        PuppetTransformer transformer = new PuppetTransformer(context);
        transformer.populateManifest();
        logger.info("Transformation to Puppet successful");
    }
}
