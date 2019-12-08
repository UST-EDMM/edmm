package io.github.edmm.plugins.juju;

import freemarker.template.Configuration;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.Operation;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.RootRelation;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static io.github.edmm.plugins.juju.JujuLifecycle.CHARM_FOLDER_PREAMBLE;

public class JujuTransformer {

    private static final Logger logger = LoggerFactory.getLogger(JujuTransformer.class);

    private static final String DEFAULT_ENV_VAR_CONNECTION = "_HOSTNAME";
    private static final String DEFAULT_TARGET_LOCATION = "localhost";

    private final TransformationContext context;
    private final Configuration cfg = TemplateHelper.forClasspath(JujuPlugin.class, "/plugins/juju");

    public JujuTransformer(TransformationContext context) {
        this.context = context;
    }

    public void generateCharms() {
        // Packaging each component stack in a different charm
        context.getModel().findComponentStacks().forEach(stack -> {
            // Setting charm name to the name of the base compute node of "stack"
            String charmName = stack.vertexSet()
                    .stream()
                    .filter(v -> v instanceof Compute)
                    .findFirst()
                    .get()
                    .getNormalizedName();
            logger.info("Generating a charm for application stack '{}'",charmName);

            // Retrieving environment variables and operations allowing to provision the nodes in "stack"
            Iterator<RootComponent> nodes = topologicalSort(stack);
            Map<String,String> envVars = new HashMap<String,String>();
            Map<String,List<Operation>> ops = new HashMap<String,List<Operation>>();
            while(nodes.hasNext()) {
                RootComponent node = nodes.next();
                if(node instanceof Compute)
                    logger.info("Skipping deployment information for (Compute) component '{}'", node.getName());
                else {
                    // Retrieving envVars for "node"
                    logger.info("Retrieving environment variables to be set for component '{}'", node.getName());
                    Map<String, Property> props = node.getProperties();
                    for(String propName : props.keySet()) {
                        Property p = props.get(propName);
                        envVars.put(node.getName().concat("_").concat(p.getName()).toUpperCase(),p.getValue());
                    }
                    for(RootRelation relation : node.getRelations()) {
                        if(relation instanceof ConnectsTo)
                            envVars.put(relation.getTarget().concat(DEFAULT_ENV_VAR_CONNECTION).toUpperCase(),
                                    DEFAULT_TARGET_LOCATION);
                    }
                    // Retrieving management operations for "node"
                    logger.info("Retrieving management operations for component '{}'", node.getName());
                    node.getOperations().keySet().forEach(opName -> {
                        if(node.getOperation(opName).isPresent()) {
                            Operation op = node.getOperation(opName).get();
                            if(!ops.containsKey(op.getName()))
                                ops.put(op.getName(), new ArrayList<Operation>());
                            if(op.hasArtifacts())
                                ops.get(opName).add(op);
                        }
                    });
                }
            }

            // Generating charm for "stack"
            generateCharm(charmName,envVars,ops);
        });
    }

    // Private method for obtaining an iterator visiting a "stack" graph in topological order
    // - Workaround needed since "TopologicalOrderIterator" was ignoring horizontal relationships
    //   after running "FindComponentStacks"
    private Iterator<RootComponent> topologicalSort(Graph<RootComponent,RootRelation> stack) {
        List<RootComponent> sortedNodes = new ArrayList<RootComponent>();
        List<RootComponent> unsortedNodes = new ArrayList<RootComponent>();
        for(RootComponent node : stack.vertexSet())
            unsortedNodes.add(node);
        while(!(unsortedNodes.isEmpty())) {
            Iterator<RootComponent> nodes = unsortedNodes.iterator();
            while(nodes.hasNext()) {
                RootComponent node = nodes.next();
                boolean dependenciesSatisfied = true;
                for(RootRelation dependency : node.getRelations()) {
                    RootComponent target = context.getModel().getComponent(dependency.getTarget()).get();
                    if(!sortedNodes.contains(target))
                        dependenciesSatisfied = false;
                }
                if(dependenciesSatisfied)
                    sortedNodes.add(node);
            }
            unsortedNodes.removeAll(sortedNodes);
        }
        return sortedNodes.iterator();
    }

    private void generateCharm(String name, Map<String,String> envVars, Map<String,List<Operation>> ops) {
        System.out.println("Charm name: " + name);
        System.out.println("  Environment Variables:");
        for (String varName : envVars.keySet()) {
            System.out.println("    - " + varName + "=" + envVars.get(varName));
        }
        for (String opName : ops.keySet()) {
            System.out.println("  Operation " + opName);
            for(Operation op : ops.get(opName)) {
                System.out.println("    - " + op.getArtifacts());
            }
        }

    }
}
