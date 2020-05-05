package io.github.edmm.plugins.azure;

import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.TargetTechnology;
import io.github.edmm.core.transformation.TransformationContext;

public class AzurePlugin extends TransformationPlugin<AzureLifecycle> {

    public static final TargetTechnology AZURE = TargetTechnology.builder().id("azure").name("Azure").build();

    public AzurePlugin() {
        super(AZURE);
    }

    @Override
    public AzureLifecycle getLifecycle(TransformationContext context) {
        return new AzureLifecycle(context);
    }
}
