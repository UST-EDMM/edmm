package io.github.edmm.plugins.ansible;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.plugins.ansible.model.AnsiblePlay;
import org.jgrapht.Graph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import static io.github.edmm.plugins.ansible.AnsibleLifecycle.FILE_NAME;

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

        try {
            Template baseTemplate = cfg.getTemplate("playbook_base.yml");
            fileAccess.append(FILE_NAME, TemplateHelper.toString(baseTemplate, null));

            CycleDetector<RootComponent, RootRelation> cycleDetector = new CycleDetector<>(context.getModel().getTopology());
            if (cycleDetector.detectCycles()) {
                // TODO handle cycle in the topology notification
                throw new RuntimeException("The given topology is not acyclic");
            } else {
                // reverse the graph to find sources
                EdgeReversedGraph<RootComponent, RootRelation> dependencyGraph = new EdgeReversedGraph<>(context.getModel().getTopology());
                // apply the topological sort
                TopologicalOrderIterator<RootComponent, RootRelation> iterator = new TopologicalOrderIterator<>(dependencyGraph);

                LOGGER.info("topological order");
                Map<String, Object> templateData = new HashMap<>();
                List<AnsiblePlay> plays = new ArrayList<>();

                while (iterator.hasNext()) {
                    RootComponent component = iterator.next();
                    LOGGER.info("Generate a play for component " + component.getName());

                    //component.getProperties().forEach(p -> {});
                    //component.getOperations();

                }

                templateData.put("plays", plays);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write Ansible file: {}", e.getMessage(), e);
        }
    }
}
