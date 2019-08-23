package io.github.edmm.plugins.ansible;

import freemarker.template.Configuration;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import org.jgrapht.Graph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class AnsibleVisitor implements ComponentVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnsibleVisitor.class);

    protected final TransformationContext context;
    protected final Configuration cfg = TemplateHelper.fromClasspath(new ClassPathResource("plugins/ansible"));
    protected final Graph<RootComponent, RootRelation> graph;

    public AnsibleVisitor(TransformationContext context) {
        this.context = context;
        this.graph = context.getTopologyGraph();
    }

    public void populateAnsibleFile() {
        PluginFileAccess fileAccess = context.getFileAccess();

        CycleDetector<RootComponent, RootRelation> cycleDetector = new CycleDetector<>(context.getModel().getTopology());
        if (cycleDetector.detectCycles()) {
            // TODO handle cycle in the topology notification
            throw new RuntimeException("The given topology is acyclic");
        } else {
            // reverse the graph to find sources
            EdgeReversedGraph<RootComponent, RootRelation> dependencyGraph = new EdgeReversedGraph<>(context.getModel().getTopology());
            // apply the topological sort
            TopologicalOrderIterator<RootComponent, RootRelation> iterator = new TopologicalOrderIterator<>(dependencyGraph);

            LOGGER.info("topological order");
            while (iterator.hasNext()) {
                RootComponent component = iterator.next();
                //component.getProperties().forEach(p -> {});
                //component.getOperations();


                LOGGER.info("Generate task for component " + component.getName());
            }
        }
    }


}
