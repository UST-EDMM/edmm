package io.github.edmm.plugins.puppet;

import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class PuppetPlugin extends TransformationPlugin<PuppetLifecycle> {

    public static final TargetTechnology PUPPET = TargetTechnology.builder().id("puppet").name("Puppet").build();

    public PuppetPlugin() {
        super(PUPPET);
    }

    @Override
    public PuppetLifecycle getLifecycle(TransformationContext context) {
        return new PuppetLifecycle(context);
    }
}
