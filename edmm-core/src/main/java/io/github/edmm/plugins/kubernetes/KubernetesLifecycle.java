package io.github.edmm.plugins.kubernetes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.TopologyGraphHelper;
import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.docker.Container;
import io.github.edmm.docker.DependencyGraph;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.VisitorHelper;
import io.github.edmm.plugins.ComputeSupportVisitor;
import io.github.edmm.plugins.kubernetes.support.DockerfileBuildingVisitor;
import io.github.edmm.plugins.kubernetes.support.ImageMappingVisitor;
import io.github.edmm.plugins.kubernetes.support.KubernetesResourceBuilder;

import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesLifecycle.class);

    protected final Graph<RootComponent, RootRelation> graph;
    protected final List<Container> containers = new ArrayList<>();

    protected DependencyGraph dependencyGraph;

    public KubernetesLifecycle(TransformationContext context) {
        super(context);
        this.graph = context.getTopologyGraph();
    }

    @Override
    public CheckModelResult checkModel() {
        ComputeSupportVisitor visitor = new ComputeSupportVisitor(context);
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        return visitor.getResult();
    }

    @Override
    public void prepare() {
        List<Compute> computeComponents = TopologyGraphHelper.getVertices(graph, Compute.class);
        for (Compute compute : computeComponents) {
            Container stack = new Container();
            stack.addComponent(compute);
            containers.add(stack);
            populateComponentStacks(graph, containers, stack, compute);
        }
        dependencyGraph = new DependencyGraph(containers, graph);
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Kubernetes...");
        PluginFileAccess fileAccess = context.getFileAccess();
        for (Container stack : containers) {
            resolveBaseImage(stack);
            buildDockerfile(stack, fileAccess);
        }
        for (Container stack : containers) {
            // Build Kubernetes resource files
            KubernetesResourceBuilder resourceBuilder = new KubernetesResourceBuilder(stack, dependencyGraph, fileAccess);
            resourceBuilder.populateResources();
        }
        logger.info("Transformation to Kubernetes successful");
    }

    protected void resolveBaseImage(Container stack) {
        ImageMappingVisitor imageMapper = new ImageMappingVisitor();
        stack.getComponents().forEach(component -> component.accept(imageMapper));
        stack.setBaseImage(imageMapper.getBaseImage());
    }

    protected void buildDockerfile(Container stack, PluginFileAccess fileAccess) {
        DockerfileBuildingVisitor dockerfileBuilder = new DockerfileBuildingVisitor(stack, fileAccess);
        dockerfileBuilder.populateDockerfile();
    }

    private void populateComponentStacks(Graph<RootComponent, RootRelation> graph, List<Container> stacks, Container stack, RootComponent component) {
        Set<RootComponent> sourceComponents = TopologyGraphHelper.getSourceComponents(graph, component, HostedOn.class);
        if (sourceComponents.size() == 1) {
            RootComponent source = sourceComponents.stream().findFirst().orElseThrow(IllegalStateException::new);
            stack.addComponent(source);
            populateComponentStacks(graph, stacks, stack, source);
        } else {
            for (RootComponent source : sourceComponents) {
                Container newStack = new Container(stack);
                newStack.addComponent(source);
                stacks.add(newStack);
                stacks.remove(stack);
                populateComponentStacks(graph, stacks, newStack, source);
            }
        }
    }
}
