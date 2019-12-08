package io.github.edmm.plugins.juju;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JujuLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(JujuLifecycle.class);

    public static final String CHARM_FOLDER_PREAMBLE = "layer-";
    public static final String HOOKS_FOLDER = "hooks";
    public static final String LAYER_FILENAME = "layer.yaml";
    public static final String METADATA_FILENAME = "metadata.yaml";

    private final TransformationContext context;

    public JujuLifecycle(TransformationContext context) {
        this.context = context;
    }

    @Override
    public void transform() {
        logger.info("Generating Juju Charms...");

        // Visit EDMM model to generate charms
        JujuTransformer transformer = new JujuTransformer(context);
        transformer.generateCharms();

        logger.info("Juju Charms successfully generated");
    }
}
