package io.github.edmm.plugins.juju;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JujuLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(JujuLifecycle.class);

    public JujuLifecycle(TransformationContext context) {
        super(context);
    }

    @Override
    public void transform() {
        logger.info("Generating Juju Charms...");
        // Visit EDMM model to generate charms
        JujuTransformer transformer = new JujuTransformer(context);
        try {
            transformer.generateCharms();
            logger.info("Juju Charms successfully generated");
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("Juju Charms generation failed -- see above error(s)");
        }
    }
}
