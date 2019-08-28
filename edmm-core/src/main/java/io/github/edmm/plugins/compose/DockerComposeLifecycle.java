package io.github.edmm.plugins.compose;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerComposeLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(DockerComposeLifecycle.class);

    private final TransformationContext context;
    private final Graph<RootComponent, RootRelation> graph;

    public DockerComposeLifecycle(TransformationContext context) {
        this.context = context;
        this.graph = context.getTopologyGraph();
    }

    @Override
    public void prepare() {

    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Docker Compose...");

        logger.info("Transformation to Docker Compose successful");
    }
}
