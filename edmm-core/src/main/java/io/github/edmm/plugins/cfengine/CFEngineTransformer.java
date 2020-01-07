package io.github.edmm.plugins.cfengine;

import freemarker.template.Configuration;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.plugin.TopologyGraphHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.component.*;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;
import io.github.edmm.model.visitor.VisitorHelper;
import io.github.edmm.plugins.cfengine.model.CFAgent;
import org.jgrapht.Graph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CFEngineTransformer implements ComponentVisitor, RelationVisitor {

    private static final Logger logger = LoggerFactory.getLogger(CFEngineTransformer.class);

    private final TransformationContext context;
    private final Configuration cfg = TemplateHelper.forClasspath(CFEnginePlugin.class, "/plugins/cfengine");

    private final Graph<RootComponent, RootRelation> graph;
    private PluginFileAccess fileAccess;
    private CFAgent agent;

    public CFEngineTransformer(TransformationContext context) {
        this.context = context;
        this.fileAccess = context.getFileAccess();
        this.graph = context.getTopologyGraph();
        agent = new CFAgent(fileAccess);
    }

    /**
     * Visit all the components in topological order and create relations
     */
    public void visitComponentsTopologicalOrder() {
        // Reverse the graph to find sources
        EdgeReversedGraph<RootComponent, RootRelation> dependencyGraph
                = new EdgeReversedGraph<>(context.getModel().getTopology());
        // Apply the topological sort
        TopologicalOrderIterator<RootComponent, RootRelation> iterator
                = new TopologicalOrderIterator<>(dependencyGraph);
        // Visit all components in topological sort
        while (iterator.hasNext()) {
            RootComponent component = iterator.next();
            logger.info("Generate a state for component " + component.getName());
            component.accept(this);
        }
        // Visit all relations
        VisitorHelper.visit(context.getModel().getRelations(), this);
    }

    public void populateCFEngineFiles() {
        agent.saveFile(cfg);
    }

    @Override
    public void visit(Compute component) {
        agent.addCompute(component);
        agent.add(component, component);
        component.setTransformed(true);
    }

    @Override
    public void visit(Tomcat component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
                .orElseThrow(TransformationException::new);
        agent.add(component, compute);
        component.setTransformed(true);
    }

    @Override
    public void visit(MysqlDatabase component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
                .orElseThrow(TransformationException::new);
        agent.add(component, compute);
        component.setTransformed(true);
    }

    @Override
    public void visit(MysqlDbms component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
                .orElseThrow(TransformationException::new);
        agent.add(component, compute);
        component.setTransformed(true);
    }

    @Override
    public void visit(WebApplication component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
                .orElseThrow(TransformationException::new);
        agent.add(component, compute);
        component.setTransformed(true);
    }

    @Override
    public void visit(ConnectsTo relation) {
        RootComponent sourceComponent = graph.getEdgeSource(relation);
        RootComponent targetComponent = graph.getEdgeTarget(relation);
        Optional<Compute> optionalSourceCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, sourceComponent);
        Optional<Compute> optionalTargetCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, targetComponent);
        // If there are compute nodes
        if (optionalSourceCompute.isPresent() && optionalTargetCompute.isPresent()) {
            agent.handleConnectRelation(targetComponent, optionalSourceCompute.get(), optionalTargetCompute.get());
        }
    }

}
