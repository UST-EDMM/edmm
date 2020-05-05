package io.github.edmm.plugins.salt;

import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class SaltStackPlugin extends TransformationPlugin<SaltStackLifecycle> {

    public static final TargetTechnology SALTSTACK = TargetTechnology.builder().id("saltstack").name("Saltstack").build();

    public SaltStackPlugin() {
        super(SALTSTACK);
    }

    @Override
    public SaltStackLifecycle getLifecycle(TransformationContext context) {
        return new SaltStackLifecycle(context);
    }
}
