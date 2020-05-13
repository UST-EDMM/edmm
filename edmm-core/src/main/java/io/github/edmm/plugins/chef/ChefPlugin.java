package io.github.edmm.plugins.chef;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.TransformationContext;

public class ChefPlugin extends TransformationPlugin<ChefLifecycle> {

    public static final DeploymentTechnology CHEF = DeploymentTechnology.builder().id("chef").name("Chef").build();

    public ChefPlugin() {
        super(CHEF);
    }

    @Override
    public ChefLifecycle getLifecycle(TransformationContext context) {
        return new ChefLifecycle(context);
    }
}
