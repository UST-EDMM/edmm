package io.github.edmm.plugins.salt;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.Platform;
import io.github.edmm.core.transformation.TransformationContext;

public class SaltStackPlugin extends Plugin<SaltStackLifecycle> {

    public static final Platform SALTSTACK = Platform.builder().id("saltstack").name("Saltstack").build();

    public SaltStackPlugin() {
        super(SALTSTACK);
    }

    @Override
    public SaltStackLifecycle getLifecycle(TransformationContext context) {
        return new SaltStackLifecycle(context);
    }
}
