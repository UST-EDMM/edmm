package io.github.edmm.plugins.kubernetes;

import io.github.edmm.core.DeploymentTechnology;
import io.github.edmm.core.execution.ExecutionContext;
import io.github.edmm.core.plugin.ExecutionPlugin;
import io.github.edmm.core.plugin.TransformationPlugin;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.parameters.InputParameter;

import com.google.common.collect.Sets;

import static io.github.edmm.model.parameters.ParameterType.NAME;

public class KubernetesPlugin extends TransformationPlugin<KubernetesLifecycle> implements ExecutionPlugin {

    public static final String STACKS_ENTRY = "stacks";
    public static final String TARGET_NAMESPACE = "target-namespace";
    public static final DeploymentTechnology KUBERNETES = DeploymentTechnology.builder()
        .id("kubernetes")
        .name("Kubernetes")
        .deploymentSupported(true)
        .executionParameters(Sets.newHashSet(
            InputParameter.of(TARGET_NAMESPACE, NAME, "default", "Kubernetes namespace for the deployment")
        ))
        .build();

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
        // Set<ParameterInstance> parameters = ParameterInstance.of(context.getUserInputs(), KUBERNETES.getTransformationParameters());
        new KubernetesExecutor(context, KUBERNETES).execute();
    }

    @Override
    public void destroy(ExecutionContext context) throws Exception {
        // noop
    }
}
