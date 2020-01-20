package io.github.edmm.plugins.chef;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class ChefPlugin extends Plugin<ChefLifecycle> {

    public static final TargetTechnology CHEF = TargetTechnology.builder().id("chef").name("Chef").build();

    public ChefPlugin() {
        super(CHEF);
    }

    @Override
    public ChefLifecycle getLifecycle(TransformationContext context) {
        return new ChefLifecycle(context);
    }
}
