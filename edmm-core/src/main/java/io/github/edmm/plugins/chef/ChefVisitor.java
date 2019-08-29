package io.github.edmm.plugins.chef;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.plugins.chef.model.Metadata;
import org.jgrapht.Graph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOKS_FOLDER;
import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOK_CHEFIGNORE_FILENAME;
import static io.github.edmm.plugins.chef.ChefLifecycle.COOKBOOK_METADATA_FILENAME;

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

                StringBuilder sb = new StringBuilder();

                Template chefIgnore = cfg.getTemplate("chefignore");

                LOGGER.info("Generate a repository structure for application stack: " + context.getModel().getName());
                fileAccess.append(COOKBOOK_CHEFIGNORE_FILENAME, TemplateHelper.toString(chefIgnore, null));
                while (iterator.hasNext()) {
                    RootComponent component = iterator.next();
                    Path cookbookPath = Paths.get(COOKBOOKS_FOLDER, component.getNormalizedName());
                    Template metadata = cfg.getTemplate("metadata.rb");
                    Map<String, Object> metadataMap = new HashMap<>();
                    metadataMap.put("metadata", Metadata.builder().name(component.getNormalizedName()).build());
                    fileAccess.append(
                            cookbookPath.resolve(COOKBOOK_METADATA_FILENAME).toString(),
                            TemplateHelper.toString(metadata, metadataMap)
                    );

                    LOGGER.info("Generate a cookbook for component " + component.getName());
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write Ansible file: {}", e.getMessage(), e);
        }
    }
}
