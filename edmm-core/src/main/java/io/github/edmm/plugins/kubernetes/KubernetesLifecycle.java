package io.github.edmm.plugins.kubernetes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.GraphHelper;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.plugins.kubernetes.model.ComponentStack;
import io.github.edmm.plugins.kubernetes.support.DependencyGraph;
import io.github.edmm.plugins.kubernetes.support.DockerfileBuildingVisitor;
import io.github.edmm.plugins.kubernetes.support.ImageMappingVisitor;
import io.github.edmm.plugins.kubernetes.support.KubernetesResourceBuilder;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesLifecycle.class);

    private final TransformationContext context;
    private final Graph<RootComponent, RootRelation> graph;

    private List<ComponentStack> componentStacks = new ArrayList<>();
    private DependencyGraph dependencyGraph;

    public KubernetesLifecycle(TransformationContext context) {
        this.context = context;
        this.graph = context.getTopologyGraph();
    }

    @Override
    public void prepare() {
        // Populate initial component stacks
        List<Compute> computeComponents = GraphHelper.getComponents(graph, Compute.class);
        for (Compute compute : computeComponents) {
            ComponentStack stack = new ComponentStack();
            stack.addComponent(compute);
            componentStacks.add(stack);
            populateComponentStacks(graph, componentStacks, stack, compute);
        }
        dependencyGraph = new DependencyGraph(componentStacks, graph);
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Kubernetes...");
        PluginFileAccess fileAccess = context.getFileAccess();
        for (ComponentStack stack : componentStacks) {
            // Resolve base image
            ImageMappingVisitor imageMapper = new ImageMappingVisitor();
            stack.getComponents().forEach(component -> component.accept(imageMapper));
            stack.setBaseImage(imageMapper.getBaseImage());
            // Build Dockerfile
            DockerfileBuildingVisitor dockerfileBuilder = new DockerfileBuildingVisitor(stack, fileAccess);
            dockerfileBuilder.populateDockerfile();
        }
        for (ComponentStack stack : componentStacks) {
            // Build Kubernetes resource files
            KubernetesResourceBuilder resourceBuilder = new KubernetesResourceBuilder(stack, dependencyGraph, fileAccess);
            resourceBuilder.populateResources();
        }
        logger.info("Transformation to Kubernetes successful");
    }

    private void populateComponentStacks(Graph<RootComponent, RootRelation> graph, List<ComponentStack> stacks, ComponentStack stack, RootComponent component) {
        Set<RootComponent> sourceComponents = GraphHelper.getSourceComponents(graph, component, HostedOn.class);
        if (sourceComponents.size() == 1) {
            RootComponent source = sourceComponents.stream().findFirst().orElseThrow(IllegalStateException::new);
            stack.addComponent(source);
            populateComponentStacks(graph, stacks, stack, source);
        } else {
            for (RootComponent source : sourceComponents) {
                ComponentStack newStack = new ComponentStack(stack);
                newStack.addComponent(source);
                stacks.add(newStack);
                stacks.remove(stack);
                populateComponentStacks(graph, stacks, newStack, source);
            }
        }
    }
}
