package io.github.edmm.plugins.heat;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.TransformationContext;

public class HeatPlugin extends TransformationPlugin<HeatLifecycle> {

    public static final DeploymentTechnology HEAT = DeploymentTechnology.builder().id("heat").name("Heat Orchestration Template").build();

    public HeatPlugin() {
        super(HEAT);
    }

    @Override
    public HeatLifecycle getLifecycle(TransformationContext context) {
        return new HeatLifecycle(context);
    }
}
