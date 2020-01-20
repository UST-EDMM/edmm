package io.github.edmm.plugins.puppet;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class PuppetPlugin extends Plugin<PuppetLifecycle> {

    public static final TargetTechnology PUPPET = TargetTechnology.builder().id("puppet").name("Puppet").build();

    public PuppetPlugin() {
        super(PUPPET);
    }

    @Override
    public PuppetLifecycle getLifecycle(TransformationContext context) {
        return new PuppetLifecycle(context);
    }
}
