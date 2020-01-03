package io.github.edmm.plugins.azure;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.Platform;
import io.github.edmm.core.transformation.TransformationContext;

public class AzurePlugin extends Plugin<AzureLifecycle> {

    public static final Platform AZURE = Platform.builder().id("azure").name("Azure").build();

    public AzurePlugin() {
        super(AZURE);
    }

    @Override
    public AzureLifecycle getLifecycle(TransformationContext context) {
        return new AzureLifecycle(context);
    }
}
