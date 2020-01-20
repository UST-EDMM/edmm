package io.github.edmm.plugins.heat;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class HeatPlugin extends Plugin<HeatLifecycle> {

    public static final TargetTechnology HEAT = TargetTechnology.builder().id("heat").name("Heat Orchestration Template").build();

    public HeatPlugin() {
        super(HEAT);
    }

    @Override
    public HeatLifecycle getLifecycle(TransformationContext context) {
        return new HeatLifecycle(context);
    }
}
