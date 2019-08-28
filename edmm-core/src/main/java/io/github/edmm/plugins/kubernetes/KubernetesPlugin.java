package io.github.edmm.plugins.kubernetes;

import io.github.edmm.core.plugin.Plugin;
import io.github.edmm.core.transformation.Platform;
import io.github.edmm.core.transformation.TransformationContext;

public class KubernetesPlugin extends Plugin<KubernetesLifecycle> {

    public static final Platform KUBERNETES = Platform.builder().id("kubernetes").name("Kubernetes").build();

    public KubernetesPlugin() {
        super(KUBERNETES);
    }

    @Override
    public KubernetesLifecycle getLifecycle(TransformationContext context) {
        return new KubernetesLifecycle(context);
    }
}
