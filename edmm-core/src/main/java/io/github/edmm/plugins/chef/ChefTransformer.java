package io.github.edmm.plugins.chef;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.Artifact;
import io.github.edmm.model.Operation;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.plugins.chef.model.Metadata;
import io.github.edmm.plugins.chef.model.PolicyFile;
import io.github.edmm.plugins.chef.model.ShellRecipe;
import org.jgrapht.Graph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOKS_FOLDER;
import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOK_CHEFIGNORE_FILENAME;
import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOK_DEFAULT_RECIPE_FILENAME;
import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOK_FILES_FOLDER;
import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOK_METADATA_FILENAME;
import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOK_RECIPES_FOLDER;
import static io.github.edmm.plugins.chef.ChefLifecycle.POLICIES_FOLDER;
import static io.github.edmm.plugins.chef.ChefLifecycle.POLICY_FILENAME;

public class ChefTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChefTransformer.class);

    protected final TransformationContext context;
    protected final Graph<RootComponent, RootRelation> graph;
    private final Configuration cfg = TemplateHelper.fromClasspath(new ClassPathResource("plugins/chef"));

    public ChefTransformer(TransformationContext context) {
        this.context = context;
        this.graph = context.getTopologyGraph();
    }

    public void populateChefRepository() {
        PluginFileAccess fileAccess = context.getFileAccess();

        try {
            CycleDetector<RootComponent, RootRelation> cycleDetector = new CycleDetector<>(context.getModel().getTopology());
            if (cycleDetector.detectCycles()) {
                throw new RuntimeException("The given topology is not acyclic");
            } else {
                // initialize templates
                Template chefIgnore = cfg.getTemplate("chefignore");

                // generate the chefignore file, currently a static file is used
                fileAccess.append(COOKBOOK_CHEFIGNORE_FILENAME, TemplateHelper.toString(chefIgnore, null));

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

                        List<String> runningOrder = new ArrayList<>();
                        Map<String, Object> templateData = new HashMap<>();
                        LOGGER.info("Generate a repository structure for application stack: " + stack.toString());

                        while (iterator.hasNext()) {
                            RootComponent component = iterator.next();
                            Path cookbookPath = Paths.get(COOKBOOKS_FOLDER, component.getNormalizedName());

                            generateMetadataFile(templateData, component, cookbookPath);

                            // store cookbook names with the reference to default recipes
                            // this lsit is concatenated for a run_list directive in a policy file
                            runningOrder.add("'" + component.getNormalizedName() + "::default'");

                            LOGGER.info("Generate a cookbook for component " + component.getName());
                            Path recipePath = cookbookPath.resolve(COOKBOOK_RECIPES_FOLDER).resolve(COOKBOOK_DEFAULT_RECIPE_FILENAME);
                            if (component instanceof Compute) {
                                LOGGER.info("generatea provisioning recipe for compute component: " + component.getName());
                                generateMachineRecipe(component, recipePath);
                            } else {
                                LOGGER.info("generate a shell recipe for component: " + component.getName());
                                generateShellRecipe(templateData, component, cookbookPath, recipePath);
                            }
                        }
                        generatePolicyFile(templateData, runningOrder, stackName);
                    } catch (IOException e) {
                        LOGGER.error("Failed to generate stacks for Chef: {}", e.getMessage(), e);
                    }
                });

            }
        } catch (IOException e) {
            LOGGER.error("Failed to write Chef cookbooks : {}", e.getMessage(), e);
        }
    }

    private void prepareProperties(Map<String, String> targetMap, Map<String, Property> properties) {
        properties.forEach((key, property) -> targetMap.put(key, property.getValue().replaceAll("\n", "")));
    }

    private List<Operation> collectOperations(RootComponent component) {
        List<Operation> operations = new ArrayList<>();
        component.getStandardLifecycle().getCreate().ifPresent(operations::add);
        component.getStandardLifecycle().getConfigure().ifPresent(operations::add);
        component.getStandardLifecycle().getStart().ifPresent(operations::add);
        return operations;
    }

    private void generateShellRecipe(Map<String, Object> templateData, RootComponent component, Path cookbookPath, Path recipePath) throws IOException {
        Template shellRecipe = cfg.getTemplate("shell_script_recipe.rb");
        List<ShellRecipe> recipes = new ArrayList<>();
        Map<String, String> properties = new HashMap<>();

        // set properties as Ruby environment variables within a recipe
        prepareProperties(properties, component.getProperties());
        templateData.put("properties", properties);

        collectOperations(component).forEach(o -> {
            if (!o.getArtifacts().isEmpty()) {
                Artifact a = o.getArtifacts().get(0);
                Path p = Paths.get(a.getValue());

                Path recipeFilePath = cookbookPath.resolve(COOKBOOK_FILES_FOLDER).resolve(p.getFileName().toString());

                try {
                    context.getFileAccess().copy(a.getValue(), recipeFilePath.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ShellRecipe recipe = ShellRecipe.builder()
                        .name(o.getNormalizedName())
                        .fileName(p.getFileName().toString())
                        .filePath(a.getValue())
                        .targetPath("/tmp/".concat(p.getFileName().toString()))
                        .sourcePath(recipeFilePath.toString().replace("\\", "/"))
                        .build();

                recipes.add(recipe);
            }
        });
        templateData.put("tasks", recipes);

        context.getFileAccess().append(recipePath.toString(), TemplateHelper.toString(shellRecipe, templateData));
    }

    private void generateMachineRecipe(RootComponent component, Path recipePath) throws IOException {
        Template machineRecipe = cfg.getTemplate("chef-provisioning.rb");
        Map<String, Object> machineData = new HashMap<>();
        machineData.put("name", component.getNormalizedName());
        String image = ((Compute) component).getMachineImage()
                .orElseThrow(() -> new TransformationException("Error transforming Machine "));
        machineData.put("image", image);

        context.getFileAccess().append(recipePath.toString(), TemplateHelper.toString(machineRecipe, machineData));
    }

    private void generateMetadataFile(Map<String, Object> templateData, RootComponent component, Path cookbookPath) throws IOException {
        Template metadata = cfg.getTemplate("metadata.rb");
        templateData.put("metadata", Metadata.builder().name(component.getNormalizedName()).build());
        context.getFileAccess().append(
                cookbookPath.resolve(COOKBOOK_METADATA_FILENAME).toString(),
                TemplateHelper.toString(metadata, templateData)
        );
    }

    private void generatePolicyFile(Map<String, Object> templateData, List<String> runningOrder, String stackName) throws IOException {
        // generate a policyfile per stack
        Template policyFile = cfg.getTemplate("Policyfile.rb");
        PolicyFile pf = PolicyFile.builder().name(context.getModel().getName()).runningOrder(String.join(", ", runningOrder)).build();
        templateData.put("policyfile", pf);

        Path policyPath = Paths.get(POLICIES_FOLDER, stackName.concat("_").concat(POLICY_FILENAME));
        context.getFileAccess().append(policyPath.toString(), TemplateHelper.toString(policyFile, templateData));
    }

}
