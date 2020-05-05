package io.github.edmm.plugins.ansible;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.TemplateHelper;
import io.github.edmm.core.TopologyGraphHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.Operation;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.plugins.ansible.model.AnsiblePlay;
import io.github.edmm.plugins.ansible.model.AnsibleTask;

import com.google.common.collect.Lists;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.plugins.ansible.AnsibleLifecycle.FILE_NAME;

public class AnsibleTransformer implements ComponentVisitor {

    private static final Logger logger = LoggerFactory.getLogger(AnsibleTransformer.class);

    private final TransformationContext context;
    private final Configuration cfg = TemplateHelper.forClasspath(AnsiblePlugin.class, "/plugins/ansible");

    public AnsibleTransformer(TransformationContext context) {
        this.context = context;
    }

    public void populateAnsibleFile() {
        PluginFileAccess fileAccess = context.getFileAccess();
        try {
            Template baseTemplate = cfg.getTemplate("playbook_base.yml");

            // Reverse the graph to find sources
            EdgeReversedGraph<RootComponent, RootRelation> dependencyGraph
                = new EdgeReversedGraph<>(context.getModel().getTopology());
            // Apply the topological sort
            TopologicalOrderIterator<RootComponent, RootRelation> iterator
                = new TopologicalOrderIterator<>(dependencyGraph);

            Map<String, Object> templateData = new HashMap<>();
            List<AnsiblePlay> plays = new ArrayList<>();

            while (iterator.hasNext()) {
                RootComponent component = iterator.next();
                logger.info("Generate a play for component " + component.getName());
                Map<String, String> properties = new HashMap<>();
                List<AnsibleTask> tasks = new ArrayList<>();

                List<RootComponent> stack = Lists.newArrayList(component);
                TopologyGraphHelper.resolveChildComponents(context.getTopologyGraph(), stack, component);

                prepareProperties(properties, stack);
                prepareTasks(tasks, collectOperations(component));

                String hosts = component.getNormalizedName();
                Optional<Compute> optionalCompute = TopologyGraphHelper.resolveHostingComputeComponent(context.getTopologyGraph(), component);
                if (optionalCompute.isPresent()) {
                    hosts = optionalCompute.get().getNormalizedName();
                }

                AnsiblePlay play = AnsiblePlay.builder()
                    .name(component.getName())
                    .hosts(hosts)
                    .vars(properties)
                    .tasks(tasks)
                    .build();

                plays.add(play);
            }

            templateData.put("plays", plays);
            fileAccess.append(FILE_NAME, TemplateHelper.toString(baseTemplate, templateData));
        } catch (IOException e) {
            logger.error("Failed to write Ansible file: {}", e.getMessage(), e);
        }
    }

    private void prepareProperties(Map<String, String> envVars, List<RootComponent> stack) {
        String[] blacklist = {"key_name", "public_key"};
        for (RootComponent component : stack) {
            Map<String, Property> properties = component.getProperties();
            properties.values().stream()
                .filter(p -> !Arrays.asList(blacklist).contains(p.getName()))
                .forEach(p -> envVars.put(component.getNormalizedName() + "_" + p.getNormalizedName(), p.getValue()));
        }
    }

    private void prepareTasks(List<AnsibleTask> targetQueue, List<Operation> operations) {
        operations.forEach(operation -> {
            if (!operation.getArtifacts().isEmpty()) {
                String file = operation.getArtifacts().get(0).getValue();
                AnsibleTask task = AnsibleTask.builder()
                    .name(operation.getNormalizedName())
                    .script(file)
                    .build();
                targetQueue.add(task);
                // Copy artifact files to target directory
                PluginFileAccess fileAccess = context.getFileAccess();
                try {
                    fileAccess.copy(file, file);
                } catch (IOException e) {
                    logger.warn("Failed to copy file '{}'", file);
                }
            }
        });
    }

    private List<Operation> collectOperations(RootComponent component) {
        List<Operation> operations = new ArrayList<>();
        component.getStandardLifecycle().getCreate().ifPresent(operations::add);
        component.getStandardLifecycle().getConfigure().ifPresent(operations::add);
        component.getStandardLifecycle().getStart().ifPresent(operations::add);
        return operations;
    }
}
