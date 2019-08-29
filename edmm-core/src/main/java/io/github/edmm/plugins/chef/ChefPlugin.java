package io.github.edmm.plugins.chef;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.Platform;
import io.github.edmm.core.transformation.TransformationContext;

public class ChefPlugin extends Plugin<ChefLifecycle> {

    public static final Platform CHEF = Platform.builder().id("plugins.chef").name("Chef").build();

    public ChefPlugin() {
        super(CHEF);
    }

    @Override
    public ChefLifecycle getLifecycle(TransformationContext context) {
        return new ChefLifecycle(context);
    }
}
