package io.github.edmm.plugins.compose;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.TransformationContext;

public class DockerComposePlugin extends TransformationPlugin<DockerComposeLifecycle> {

    public static final DeploymentTechnology DOCKER_COMPOSE = DeploymentTechnology.builder().id("compose").name("Docker Compose").build();

    public DockerComposePlugin() {
        super(DOCKER_COMPOSE);
    }

    @Override
    public DockerComposeLifecycle getLifecycle(TransformationContext context) {
        return new DockerComposeLifecycle(context);
    }
}
