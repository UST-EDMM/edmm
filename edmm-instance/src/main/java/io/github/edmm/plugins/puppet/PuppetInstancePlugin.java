package io.github.edmm.plugins.puppet;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;

public class PuppetInstancePlugin extends InstancePlugin<PuppetInstancePluginLifecycle> {

    private static final SourceTechnology PUPPET = SourceTechnology.builder().id("puppet").name("Puppet").build();

    public PuppetInstancePlugin() {
        super(PUPPET);
    }

    @Override
    public PuppetInstancePluginLifecycle getLifecycle(InstanceTransformationContext context) {
        return new PuppetInstancePluginLifecycle(context);
    }
}
