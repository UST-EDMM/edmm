package io.github.edmm.plugins.kubernetes;

import io.github.edmm.core.plugin.InstancePlugin;
import io.github.edmm.core.transformation.InstanceTransformationContext;
import io.github.edmm.core.transformation.SourceTechnology;

public class KubernetesInstancePlugin extends InstancePlugin<KubernetesInstancePluginLifecycle> {

    private static final SourceTechnology KUBERNETES = SourceTechnology.builder().id("kubernetes").name("Kubernetes").build();

    public KubernetesInstancePlugin() {
        super(KUBERNETES);
    }

    @Override
    public KubernetesInstancePluginLifecycle getLifecycle(InstanceTransformationContext context) {
        return new KubernetesInstancePluginLifecycle(context);
    }
}
