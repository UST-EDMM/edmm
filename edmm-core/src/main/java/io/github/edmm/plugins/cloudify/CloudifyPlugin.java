package io.github.edmm.plugins.cloudify;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class CloudifyPlugin extends Plugin<CloudifyLifecycle> {

    public static final TargetTechnology CLOUDIFY = TargetTechnology.builder().id("cloudify").name("Cloudify").build();

    public CloudifyPlugin() {
        super(CLOUDIFY);
    }

    @Override
    public CloudifyLifecycle getLifecycle(TransformationContext context) {
        return new CloudifyLifecycle(context);
    }
}
