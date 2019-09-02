package io.github.edmm.plugins.puppet;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.Artifact;
import io.github.edmm.model.Operation;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.plugins.puppet.model.Task;
import org.jgrapht.Graph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import static io.github.edmm.plugins.puppet.PuppetLifecycle.MANIFEST_EXTENSION;
import static io.github.edmm.plugins.puppet.PuppetLifecycle.MANIFEST_MAIN;
import static io.github.edmm.plugins.puppet.PuppetLifecycle.MODULE_FILES_FOLDER;
import static io.github.edmm.plugins.puppet.PuppetLifecycle.MODULE_MANIFESTS_FOLDER;

public class PuppetTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PuppetTransformer.class);

    protected final TransformationContext context;
    protected final Graph<RootComponent, RootRelation> graph;
    private final Configuration cfg = TemplateHelper.fromClasspath(new ClassPathResource("plugins/puppet"));

    public PuppetTransformer(TransformationContext context) {
        this.context = context;
        this.graph = context.getTopologyGraph();
    }

    public void populateManifest() {
        PluginFileAccess fileAccess = context.getFileAccess();

        try {
            CycleDetector<RootComponent, RootRelation> cycleDetector = new CycleDetector<>(context.getModel().getTopology());
            if (cycleDetector.detectCycles()) {
                throw new RuntimeException("The given topology is not acyclic");
            } else {
                context.getModel().findComponentStacks().forEach(stack -> {
                    try {
                        // TODO check if compute node is present in the stack
                        String stackName = stack.vertexSet()
                                .stream()
                                .filter(v -> v instanceof Compute)
                                .findFirst()
                                .get()
                                .getNormalizedName();
                        // sort the reversed topology topologically to have a global order
                        TopologicalOrderIterator<RootComponent, RootRelation> iterator = new TopologicalOrderIterator<>(stack);

                        LOGGER.info("Generate a repository structure for application stack: " + stackName);

                        while (iterator.hasNext()) {
                            RootComponent component = iterator.next();
                            LOGGER.info("Generate a cookbook for component " + component.getName());
                            if (component instanceof Compute) {
                                LOGGER.info("ignore generating a module for compute component: " + component.getName());
                            } else {
                                LOGGER.info("generate task modules for component: " + component.getName());
                                generateComponentModule(component);
                            }
                        }
                    } catch (IOException e) {
                        LOGGER.error("Failed to generate stacks for Chef: {}", e.getMessage(), e);
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.error("Failed to write Chef cookbooks : {}", e.getMessage(), e);
        }
    }

    private String prepareProperties(Map<String, Property> properties) {
        StringBuilder sb = new StringBuilder();
        properties.forEach((key, property) -> {
            sb.append(key);
            sb.append("=");
            sb.append(property.getValue().replaceAll("\n", ""));
            sb.append(",");
        });
        return sb.toString();
    }

    private List<Operation> collectOperations(RootComponent component) {
        List<Operation> operations = new ArrayList<>();
        component.getStandardLifecycle().getCreate().ifPresent(operations::add);
        component.getStandardLifecycle().getConfigure().ifPresent(operations::add);
        component.getStandardLifecycle().getStart().ifPresent(operations::add);
        return operations;
    }

    private void generateComponentModule(RootComponent component) throws IOException {
        Template componentTemplate = cfg.getTemplate("component_template.pp");
        Template taskTemplate = cfg.getTemplate("task_template.pp");
        Path componentManifestsFolder = Paths.get(component.getNormalizedName(), MODULE_MANIFESTS_FOLDER);
        Path componentFilesFolder = Paths.get(component.getNormalizedName(), MODULE_FILES_FOLDER);

        Path componentClassPath = componentManifestsFolder.resolve(MANIFEST_MAIN.concat(MANIFEST_EXTENSION));

        Map<String, Object> componentData = new HashMap<>();
        String propsString = prepareProperties(component.getProperties());

        componentData.put("component", component.getNormalizedName());
        List<Task> tasks = new ArrayList<>();

        collectOperations(component).forEach(o -> {
            if (!o.getArtifacts().isEmpty()) {
                Map<String, Object> taskData = new HashMap<>();
                Path taskClassPath = componentManifestsFolder.resolve(o.getNormalizedName().concat(MANIFEST_EXTENSION));
                taskData.put("component", component.getNormalizedName());
                Artifact a = o.getArtifacts().get(0);
                Path p = Paths.get(a.getValue());

                Task t = Task.builder()
                        .name(o.getNormalizedName())
                        .varString(propsString)
                        .scriptFileName(p.getFileName().toString())
                        .build();

                tasks.add(t);
                taskData.put("task", t);

                try {
                    context.getFileAccess().copy(a.getValue(), componentFilesFolder.resolve(p.getFileName().toString()).toString());

                    context.getFileAccess().append(taskClassPath.toString(), TemplateHelper.toString(taskTemplate, taskData));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        componentData.put("tasks", tasks);
        context.getFileAccess().append(componentClassPath.toString(), TemplateHelper.toString(componentTemplate, componentData));
    }
}
