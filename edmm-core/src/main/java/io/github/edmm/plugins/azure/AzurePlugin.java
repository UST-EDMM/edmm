package io.github.edmm.plugins.azure;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.TransformationContext;

public class AzurePlugin extends TransformationPlugin<AzureLifecycle> {

    public static final DeploymentTechnology AZURE = DeploymentTechnology.builder().id("azure").name("Azure").build();

    public AzurePlugin() {
        super(AZURE);
    }

    @Override
    public AzureLifecycle getLifecycle(TransformationContext context) {
        return new AzureLifecycle(context);
    }
}
