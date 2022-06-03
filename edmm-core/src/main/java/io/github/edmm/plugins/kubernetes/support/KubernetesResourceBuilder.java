package io.github.edmm.plugins.kubernetes.support;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.core.TopologyGraphHelper;
import io.github.edmm.core.TransformationHelper;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.docker.Container;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.plugins.kubernetes.model.ConfigMapResource;
import io.github.edmm.plugins.kubernetes.model.DeploymentResource;
import io.github.edmm.plugins.kubernetes.model.KubernetesResource;
import io.github.edmm.plugins.kubernetes.model.ServiceResource;

import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesResourceBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DockerfileBuildingVisitor.class);

    private final List<KubernetesResource> resources = new ArrayList<>();

    private final Container stack;
    private final Graph<RootComponent, RootRelation> graph;
    private final PluginFileAccess fileAccess;

    public KubernetesResourceBuilder(Container stack, Graph<RootComponent, RootRelation> graph, PluginFileAccess fileAccess) {
        this.stack = stack;
        this.graph = graph;
        this.fileAccess = fileAccess;
    }

    public void populateResources() {
        resolveEnvVars();
        resources.add(new DeploymentResource(stack));
        if (stack.getPorts().size() > 0) {
            resources.add(new ServiceResource(stack));
        }
        resources.add(new ConfigMapResource(stack, TopologyGraphHelper.resolveComputedProperties(graph, stack.getRoot())));
        resources.forEach(KubernetesResource::build);
        try {
            String targetDirectory = stack.getLabel();
            for (KubernetesResource resource : resources) {
                fileAccess.write(targetDirectory + "/" + resource.getName() + ".yaml", resource.toYaml());
            }
        } catch (Exception e) {
            logger.error("Failed to create Kubernetes resource files for stack '{}'", stack.getName(), e);
            throw new TransformationException(e);
        }
    }

    private void resolveEnvVars() {
        var properties = TopologyGraphHelper.resolveComponentStackProperties(graph, stack.getRoot());
        for (var entry : properties.entrySet()) {
            var p = entry.getValue();
            if (TransformationHelper.matchesBlacklist(entry.getKey())) {
                continue;
            }
            var name = entry.getKey().toUpperCase();
            if (p.isComputed() || p.getValue() == null || p.getValue().startsWith("$")) {
                stack.addRuntimeEnvVar(name);
            } else {
                stack.addEnvVar(name, p.getValue());
            }
        }
    }
}
