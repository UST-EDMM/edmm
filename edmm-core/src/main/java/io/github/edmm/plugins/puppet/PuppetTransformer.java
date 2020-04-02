package io.github.edmm.plugins.puppet;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.plugin.TopologyGraphHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.Artifact;
import io.github.edmm.model.Operation;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.plugins.puppet.model.Task;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.plugins.puppet.PuppetLifecycle.MANIFEST_EXTENSION;
import static io.github.edmm.plugins.puppet.PuppetLifecycle.MANIFEST_MAIN;
import static io.github.edmm.plugins.puppet.PuppetLifecycle.MODULE_FILES_FOLDER;
import static io.github.edmm.plugins.puppet.PuppetLifecycle.MODULE_MANIFESTS_FOLDER;

public class PuppetTransformer {

    private static final Logger logger = LoggerFactory.getLogger(PuppetTransformer.class);

    private final TransformationContext context;
    private final Configuration cfg = TemplateHelper.forClasspath(PuppetPlugin.class, "/plugins/puppet");

    public PuppetTransformer(TransformationContext context) {
        this.context = context;
    }

    public void populateManifest() {
        try {
            context.getModel().findComponentStacks().forEach(stack -> {
                try {
                    // TODO check if compute node is present in the stack
                    String stackName = stack.vertexSet()
                        .stream()
                        .filter(v -> v instanceof Compute)
                        .findFirst()
                        .get()
                        .getNormalizedName();
                    logger.info("Generate a repository structure for application stack '{}'", stackName);

                    // Sort the reversed topology topologically to have a global order
                    TopologicalOrderIterator<RootComponent, RootRelation> iterator = new TopologicalOrderIterator<>(stack);

                    while (iterator.hasNext()) {
                        RootComponent component = iterator.next();
                        logger.info("Generate a cookbook for component '{}'", component.getName());
                        if (component instanceof Compute) {
                            logger.info("Ignore generating a module for compute component '{}'", component.getName());
                        } else {
                            logger.info("Generate task modules for component '{}'", component.getName());
                            generateComponentModule(component);
                        }
                    }
                } catch (IOException e) {
                    logger.error("Failed to generate stacks: {}", e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            logger.error("Failed to generate Puppet files: {}", e.getMessage(), e);
        }
    }

    private List<String> prepareProperties(List<RootComponent> stack) {
        String[] blacklist = {"key_name", "public_key"};
        List<String> envVars = new ArrayList<>();
        for (RootComponent component : stack) {
            Map<String, Property> properties = component.getProperties();
            properties.values().stream()
                .filter(p -> !Arrays.asList(blacklist).contains(p.getName()))
                .forEach(p -> envVars.add(String.format("%s=%s", component.getNormalizedName() + "_" + p.getNormalizedName(), p.getValue())));
        }
        return envVars;
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

        List<RootComponent> stack = Lists.newArrayList(component);
        TopologyGraphHelper.resolveChildComponents(context.getTopologyGraph(), stack, component);

        Map<String, Object> componentData = new HashMap<>();
        List<String> envVars = prepareProperties(stack);

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
                    .envVars(envVars)
                    .scriptFileName(p.getFileName().toString())
                    .build();

                tasks.add(t);
                taskData.put("task", t);

                try {
                    context.getFileAccess().copy(a.getValue(), componentFilesFolder.resolve(p.getFileName().toString()).toString());
                    context.getFileAccess().append(taskClassPath.toString(), TemplateHelper.toString(taskTemplate, taskData));
                } catch (IOException e) {
                    logger.error("Failed to create modules", e);
                    throw new TransformationException(e);
                }
            }
        });
        componentData.put("tasks", tasks);
        context.getFileAccess().append(componentClassPath.toString(), TemplateHelper.toString(componentTemplate, componentData));
    }
}
