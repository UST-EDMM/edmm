package io.github.edmm.plugins.compose;

import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class DockerComposePlugin extends TransformationPlugin<DockerComposeLifecycle> {

    public static final TargetTechnology DOCKER_COMPOSE = TargetTechnology.builder().id("compose").name("Docker Compose").build();

    public DockerComposePlugin() {
        super(DOCKER_COMPOSE);
    }

    @Override
    public DockerComposeLifecycle getLifecycle(TransformationContext context) {
        return new DockerComposeLifecycle(context);
    }
}
