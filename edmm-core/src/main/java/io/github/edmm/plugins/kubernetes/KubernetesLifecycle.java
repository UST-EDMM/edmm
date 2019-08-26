package io.github.edmm.plugins.kubernetes;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesLifecycle.class);

    private final TransformationContext context;

    public KubernetesLifecycle(TransformationContext context) {
        this.context = context;
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Kubernetes...");
        logger.info("Transformation to Kubernetes successful");
    }
}
