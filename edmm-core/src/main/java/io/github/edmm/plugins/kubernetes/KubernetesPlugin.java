package io.github.edmm.plugins.kubernetes;

import io.github.edmm.core.TargetTechnology;
import io.github.edmm.core.execution.ExecutionContext;
import io.github.edmm.core.plugin.ExecutionPlugin;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.TransformationContext;

public class KubernetesPlugin extends TransformationPlugin<KubernetesLifecycle> implements ExecutionPlugin {

    public static final String STACKS_ENTRY = "stacks";
    public static final TargetTechnology KUBERNETES = TargetTechnology.builder().id("kubernetes").name("Kubernetes").build();

    public KubernetesPlugin() {
        super(KUBERNETES);
    }

    @Override
    public KubernetesLifecycle getLifecycle(TransformationContext context) {
        return new KubernetesLifecycle(context);
    }

    @Override
    public void execute(ExecutionContext context) {
        // TODO
        System.out.println("foo bar baz qux doo");
    }
}
