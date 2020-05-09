package io.github.edmm.plugins.cloudify;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.TransformationContext;

public class CloudifyPlugin extends TransformationPlugin<CloudifyLifecycle> {

    public static final DeploymentTechnology CLOUDIFY = DeploymentTechnology.builder().id("cloudify").name("Cloudify").build();

    public CloudifyPlugin() {
        super(CLOUDIFY);
    }

    @Override
    public CloudifyLifecycle getLifecycle(TransformationContext context) {
        return new CloudifyLifecycle(context);
    }
}
