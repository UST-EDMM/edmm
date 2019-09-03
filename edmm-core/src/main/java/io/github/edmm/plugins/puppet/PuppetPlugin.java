package io.github.edmm.plugins.puppet;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.Platform;
import io.github.edmm.core.transformation.TransformationContext;

public class PuppetPlugin extends Plugin<PuppetLifecycle> {

    public static final Platform PUPPET = Platform.builder().id("puppet").name("Puppet").build();

    public PuppetPlugin() {
        super(PUPPET);
    }

    @Override
    public PuppetLifecycle getLifecycle(TransformationContext context) {
        return new PuppetLifecycle(context);
    }
}
