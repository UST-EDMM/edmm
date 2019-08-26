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
import io.github.edmm.model.Operation;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.plugins.ansible.model.AnsiblePlay;
import io.github.edmm.plugins.ansible.model.AnsibleTask;
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
                    Map<String, String> properties = new HashMap<>();
                    List<AnsibleTask> tasks = new ArrayList<>();

                    prepareProperties(properties, component.getProperties());
                    prepareTasks(tasks, component.getOperations());

                    AnsiblePlay play = AnsiblePlay.builder()
                            .name(component.getName())
                            .hosts("")
                            .vars(properties)
                            .tasks(tasks)
                            .build();

                    plays.add(play);
                }

                templateData.put("plays", plays);
                fileAccess.append(FILE_NAME, TemplateHelper.toString(baseTemplate, templateData));
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write Ansible file: {}", e.getMessage(), e);
        }
    }

    private void prepareProperties(Map<String, String> targetMap, Map<String, Property> properties) {
        properties.forEach((key, property) -> targetMap.put(key, property.getValue()));
    }

    private void prepareTasks(List<AnsibleTask> targetQueue, Map<String, Operation> operations) {
        operations.forEach((key, operation) -> {
            if (!operation.getArtifacts().isEmpty()) {
                AnsibleTask task = AnsibleTask.builder()
                        .name(operation.getNormalizedName())
                        .script(operation.getArtifacts().get(0).getValue())
                        .build();

                targetQueue.add(task);
            }
        });
    }
}
