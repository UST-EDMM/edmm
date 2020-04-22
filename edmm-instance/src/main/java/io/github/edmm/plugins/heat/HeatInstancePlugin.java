package io.github.edmm.plugins.heat;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.SourceTechnology;
import io.github.edmm.core.transformation.InstanceTransformationContext;

public class HeatInstancePlugin extends InstancePlugin<HeatInstancePluginLifecycle> {

    private static final SourceTechnology HEAT = SourceTechnology.builder().id("heat").name("Heat").build();

    public HeatInstancePlugin() {
        super(HEAT);
    }

    @Override
    public HeatInstancePluginLifecycle getLifecycle(InstanceTransformationContext context) {
        return new HeatInstancePluginLifecycle(context);
    }
}
