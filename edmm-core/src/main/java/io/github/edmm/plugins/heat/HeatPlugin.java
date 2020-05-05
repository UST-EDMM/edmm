package io.github.edmm.plugins.heat;

import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class HeatPlugin extends TransformationPlugin<HeatLifecycle> {

    public static final TargetTechnology HEAT = TargetTechnology.builder().id("heat").name("Heat Orchestration Template").build();

    public HeatPlugin() {
        super(HEAT);
    }

    @Override
    public HeatLifecycle getLifecycle(TransformationContext context) {
        return new HeatLifecycle(context);
    }
}
