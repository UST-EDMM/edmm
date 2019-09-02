package io.github.edmm.plugins.puppet;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuppetLifecycle extends AbstractLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(PuppetLifecycle.class);
    public static final String MODULE_FILES_FOLDER = "files";
    public static final String MODULE_MANIFESTS_FOLDER = "manifests";
    public static final String MANIFEST_MAIN = "init";
    public static final String MANIFEST_EXTENSION = ".pp";

    private final TransformationContext context;

    public PuppetLifecycle(TransformationContext context) {
        this.context = context;
    }

    @Override
    public void prepare() {
        LOGGER.info("Prepare transformation for Puppet...");
    }

    @Override
    public void transform() {
        LOGGER.info("Begin transformation to Puppet...");
        PuppetTransformer transformer = new PuppetTransformer(context);
        transformer.populateManifest();
        LOGGER.info("Transformation to Puppet successful");
    }

    @Override
    public void cleanup() {
        LOGGER.info("Cleanup transformation leftovers...");
    }
}
