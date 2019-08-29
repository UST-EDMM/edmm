package io.github.edmm.plugins.chef;

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
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.plugins.chef.model.Metadata;
import io.github.edmm.plugins.chef.model.PolicyFile;
import io.github.edmm.plugins.chef.model.ShellRecipe;
import org.jgrapht.Graph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOKS_FOLDER;
import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOK_CHEFIGNORE_FILENAME;
import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOK_DEFAULT_RECIPE_FILENAME;
import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOK_FILES_FOLDER;
import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOK_METADATA_FILENAME;
import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOK_RECIPES_FOLDER;
import static io.github.edmm.plugins.chef.ChefLifecycle.POLICIES_FOLDER;
import static io.github.edmm.plugins.chef.ChefLifecycle.POLICY_FILENAME;

public class ChefVisitor implements ComponentVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChefVisitor.class);

    protected final TransformationContext context;
    protected final Configuration cfg = TemplateHelper.fromClasspath(new ClassPathResource("plugins/chef"));
    protected final Graph<RootComponent, RootRelation> graph;

    public ChefVisitor(TransformationContext context) {
        this.context = context;
        this.graph = context.getTopologyGraph();
    }

    public void populateChefRepository() {
        PluginFileAccess fileAccess = context.getFileAccess();

        try {
            CycleDetector<RootComponent, RootRelation> cycleDetector = new CycleDetector<>(context.getModel().getTopology());
            if (cycleDetector.detectCycles()) {
                // TODO handle cycle in the topology notification
                throw new RuntimeException("The given topology is not acyclic");
            } else {
                // reverse the graph to find sources
                EdgeReversedGraph<RootComponent, RootRelation> dependencyGraph = new EdgeReversedGraph<>(context.getModel().getTopology());
                // apply the topological sort
                TopologicalOrderIterator<RootComponent, RootRelation> iterator = new TopologicalOrderIterator<>(dependencyGraph);

                List<String> runningOrder = new ArrayList<>();
                Map<String, Object> templateData = new HashMap<>();

                Template chefIgnore = cfg.getTemplate("chefignore");

                LOGGER.info("Generate a repository structure for application stack: " + context.getModel().getName());
                fileAccess.append(COOKBOOK_CHEFIGNORE_FILENAME, TemplateHelper.toString(chefIgnore, null));
                while (iterator.hasNext()) {
                    RootComponent component = iterator.next();
                    Path cookbookPath = Paths.get(COOKBOOKS_FOLDER, component.getNormalizedName());
                    Template metadata = cfg.getTemplate("metadata.rb");
                    templateData.put("metadata", Metadata.builder().name(component.getNormalizedName()).build());
                    fileAccess.append(
                            cookbookPath.resolve(COOKBOOK_METADATA_FILENAME).toString(),
                            TemplateHelper.toString(metadata, templateData)
                    );

                    // store cookbook names with the reference to default recipes
                    // this lsit is concatenated for a run_list directive in a policy file
                    runningOrder.add("'" + component.getNormalizedName() + "::default'");

                    LOGGER.info("Generate a cookbook for component " + component.getName());
                    if (component instanceof Compute) {
                        LOGGER.info("found compute component " + component.getName());
                        // TODO generate a chef-provisioning cookbook

                    } else {
                        // TODO generate shell script recipes
                        Path recipePath = cookbookPath.resolve(COOKBOOK_RECIPES_FOLDER).resolve(COOKBOOK_DEFAULT_RECIPE_FILENAME);
                        Template shellRecipe = cfg.getTemplate("shell_script_recipe.rb");
                        List<ShellRecipe> recipes = new ArrayList<>();

                        collectOperations(component).forEach(o -> {
                            if (!o.getArtifacts().isEmpty()) {
                                Artifact a = o.getArtifacts().get(0);
                                Path p = Paths.get(a.getValue());

                                ShellRecipe recipe = ShellRecipe.builder()
                                        .name(o.getNormalizedName())
                                        .fileName(p.getFileName().toString())
                                        .filePath(a.getValue())
                                        .targetPath("/tmp/".concat(p.getFileName().toString()))
                                        .sourcePath(cookbookPath.resolve(COOKBOOK_FILES_FOLDER).resolve(p.getFileName().toString()).toString())
                                        .build();

                                recipes.add(recipe);
                            }
                        });
                        templateData.put("tasks", recipes);

                        fileAccess.append(recipePath.toString(), TemplateHelper.toString(shellRecipe, templateData));
                    }
                }

                Template policyFile = cfg.getTemplate("Policyfile.rb");
                PolicyFile pf = PolicyFile.builder().name(context.getModel().getName()).runningOrder(String.join(", ", runningOrder)).build();
                templateData.put("policyfile", pf);

                Path policyPath = Paths.get(POLICIES_FOLDER, POLICY_FILENAME);
                fileAccess.append(policyPath.toString(), TemplateHelper.toString(policyFile, templateData));
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write Ansible file: {}", e.getMessage(), e);
        }
    }

    private List<Operation> collectOperations(RootComponent component) {
        List<Operation> operations = new ArrayList<>();

        component.getStandardLifecycle().getCreate().ifPresent(operations::add);
        component.getStandardLifecycle().getConfigure().ifPresent(operations::add);
        component.getStandardLifecycle().getStart().ifPresent(operations::add);
        return operations;
    }
}
