package io.github.edmm.plugins.ansible;

import freemarker.template.Configuration;
import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class AnsibleLifecycle extends AbstractLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnsibleLifecycle.class);

    private final TransformationContext context;
    private DeploymentModel model;
    private Configuration cfg;

    public AnsibleLifecycle(TransformationContext context) {
        this.context = context;
        this.model = context.getModel();
        //this.cfg = TemplateHelper.fromClasspath(new ClassPathResource("plugins/ansible"));
    }

    @Override
    public void prepare() {
        LOGGER.info("Prepare transformation for Ansible...");
    }

    @Override
    public void transform() {
        LOGGER.info("Begin transformation to Ansible...");
        PluginFileAccess fileAccess = context.getFileAccess();

        CycleDetector<RootComponent, RootRelation> cycleDetector = new CycleDetector<>(model.getTopology());
        if (cycleDetector.detectCycles()) {
            // TODO handle cycle in the topology notification
            throw new RuntimeException("The given topology is acyclic");
        } else {
            // reverse the graph to find sources
            EdgeReversedGraph<RootComponent, RootRelation> dependencyGraph = new EdgeReversedGraph<>(model.getTopology());
            // apply topological sort
            TopologicalOrderIterator<RootComponent, RootRelation> iterator = new TopologicalOrderIterator<>(dependencyGraph);
            LOGGER.info("topological order");
            while (iterator.hasNext()) {
                LOGGER.info(iterator.next().getName());
            }
        }

        try {
            // TODO
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("Transformation to Ansible successful");
    }

    @Override
    public void cleanup() {
        LOGGER.info("Cleanup transformation leftovers...");
    }




}
