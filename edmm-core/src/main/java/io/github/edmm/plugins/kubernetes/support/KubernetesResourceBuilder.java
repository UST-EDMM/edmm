package io.github.edmm.plugins.kubernetes.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.TopologyGraphHelper;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.docker.Container;
import io.github.edmm.docker.DependencyGraph;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.plugins.kubernetes.model.DeploymentResource;
import io.github.edmm.plugins.kubernetes.model.KubernetesResource;
import io.github.edmm.plugins.kubernetes.model.ServiceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesResourceBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DockerfileBuildingVisitor.class);

    private final List<KubernetesResource> resources = new ArrayList<>();

    private final Container stack;
    private final DependencyGraph dependencyGraph;
    private final PluginFileAccess fileAccess;

    public KubernetesResourceBuilder(Container stack, DependencyGraph dependencyGraph, PluginFileAccess fileAccess) {
        this.stack = stack;
        this.dependencyGraph = dependencyGraph;
        this.fileAccess = fileAccess;
    }

    public void populateResources() {
        resolveEnvVars();
        resources.add(new DeploymentResource(stack));
        if (stack.getPorts().size() > 0) {
            resources.add(new ServiceResource(stack));
        }
        resources.forEach(KubernetesResource::build);
        try {
            String targetDirectory = stack.getName();
            for (KubernetesResource resource : resources) {
                fileAccess.write(targetDirectory + "/" + resource.getName() + ".yaml", resource.toYaml());
            }
        } catch (Exception e) {
            logger.error("Failed to create Kubernetes resource files for stack '{}'", stack.getName(), e);
            throw new TransformationException(e);
        }
    }

    private void resolveEnvVars() {
        Set<Container> targetStacks = TopologyGraphHelper.getTargetComponents(dependencyGraph, stack, ConnectsTo.class);
        for (Container target : targetStacks) {
            for (Map.Entry<String, String> envVar : target.getEnvVars().entrySet()) {
                stack.addEnvVar(envVar.getKey(), envVar.getValue());
            }
            stack.addEnvVar((target.getName() + "_HOSTNAME").toUpperCase(), target.getServiceName());
        }
    }
}
