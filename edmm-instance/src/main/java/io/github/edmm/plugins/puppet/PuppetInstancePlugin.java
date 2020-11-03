package io.github.edmm.plugins.puppet;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;

public class PuppetInstancePlugin extends InstancePlugin<PuppetInstancePluginLifecycle> {

    private static final SourceTechnology PUPPET = SourceTechnology.builder().id("puppet").name("Puppet").build();
    private final InstanceTransformationContext context;

    public PuppetInstancePlugin(InstanceTransformationContext context) {
        super(PUPPET);
        this.context = context;
    }

    @Override
    public PuppetInstancePluginLifecycle getLifecycle(InstanceTransformationContext context) {
        return null;
    }
}
