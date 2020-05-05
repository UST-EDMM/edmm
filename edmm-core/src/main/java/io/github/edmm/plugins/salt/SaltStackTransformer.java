package io.github.edmm.plugins.salt;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.TemplateHelper;
import io.github.edmm.core.TopologyGraphHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.MysqlDatabase;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.Tomcat;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;
import io.github.edmm.model.visitor.VisitorHelper;
import io.github.edmm.plugins.salt.model.SaltBase;
import io.github.edmm.plugins.salt.model.SaltFormula;

import freemarker.template.Configuration;
import org.jgrapht.Graph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaltStackTransformer implements ComponentVisitor, RelationVisitor {

    private static final Logger logger = LoggerFactory.getLogger(SaltStackTransformer.class);

    private final TransformationContext context;
    private final Configuration cfg = TemplateHelper.forClasspath(SaltStackPlugin.class, "/plugins/salt");

    // Used to generate static ip
    private final IpGenerator ipGenerator;
    private final Graph<RootComponent, RootRelation> graph;
    private final SaltBase baseFile;
    // <ComputeName, Formula>
    private final Map<String, SaltFormula> formulas = new HashMap<>();
    private final PluginFileAccess fileAccess;

    public SaltStackTransformer(TransformationContext context) {
        this.context = context;
        this.fileAccess = context.getFileAccess();
        this.baseFile = new SaltBase(fileAccess, cfg);
        this.graph = context.getTopologyGraph();
        this.ipGenerator = new IpGenerator("10.0.0.0", "10.0.0.255");
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

    public void populateSaltFiles() {
        baseFile.saveFile();
        for (Map.Entry<String, SaltFormula> f : formulas.entrySet()) {
            f.getValue().saveFile();
        }
        // Try Pillar
        //SaltPillar sp = new SaltPillar("try", fileAccess, cfg);
        //sp.saveFile();
    }

    @Override
    public void visit(Compute component) {
        baseFile.addMinion(component);
        try {
            SaltFormula formula = new SaltFormula(component.getName(), ipGenerator.getNextIp(), fileAccess, cfg);
            formulas.put(component.getNormalizedName(), formula);
            formulas.get(component.getNormalizedName()).add(component);
            component.setTransformed(true);
        } catch (AllUsedIpsException e) {
            logger.error("Failed to generate ip for Salt file: {}", e.getMessage(), e);
        }
    }

    @Override
    public void visit(Tomcat component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
            .orElseThrow(TransformationException::new);
        formulas.get(compute.getNormalizedName()).add(component);
        component.setTransformed(true);
    }

    @Override
    public void visit(MysqlDatabase component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
            .orElseThrow(TransformationException::new);
        formulas.get(compute.getNormalizedName()).add(component);
        component.setTransformed(true);
    }

    @Override
    public void visit(MysqlDbms component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
            .orElseThrow(TransformationException::new);
        formulas.get(compute.getNormalizedName()).add(component);
        component.setTransformed(true);
    }

    @Override
    public void visit(WebApplication component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
            .orElseThrow(TransformationException::new);
        formulas.get(compute.getNormalizedName()).add(component);
        component.setTransformed(true);
    }

    @Override
    public void visit(ConnectsTo relation) {
        handleRelation(relation);
    }

    /**
     * Add env vars to the target component
     *
     * @param relation ConnectsTo relation
     */
    private void handleRelation(ConnectsTo relation) {
        RootComponent sourceComponent = graph.getEdgeSource(relation);
        RootComponent targetComponent = graph.getEdgeTarget(relation);
        Optional<Compute> optionalSourceCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, sourceComponent);
        Optional<Compute> optionalTargetCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, targetComponent);
        // If there are compute nodes
        if (optionalSourceCompute.isPresent() && optionalTargetCompute.isPresent()) {
            SaltFormula sourceFormula = formulas.get(optionalSourceCompute.get().getNormalizedName());
            SaltFormula targetFormula = formulas.get(optionalTargetCompute.get().getNormalizedName());
            String name = targetComponent.getNormalizedName().toUpperCase() + "_HOSTNAME";
            sourceFormula.addEnvVar(name, targetFormula.hostName);
            if (sourceFormula != targetFormula) {
                // Add all the variables of targetComponent + those of the underlying nodes
                sourceFormula.copyComponentEnvVar(targetFormula, targetComponent);
            }
        }
    }
}
