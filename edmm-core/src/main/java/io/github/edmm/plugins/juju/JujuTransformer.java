package io.github.edmm.plugins.juju;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.github.edmm.core.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.Artifact;
import io.github.edmm.model.Operation;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.plugins.juju.model.EnvironmentVariable;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JujuTransformer {

    public static final String CHARM_FOLDER_PREAMBLE = "layer-";
    public static final String CHARM_SERIES = "xenial";
    public static final String HOOKS_FOLDER = "hooks";
    public static final String HOOK_INSTALL = "install";
    public static final String LAYER_FILENAME = "layer.yaml";
    public static final String METADATA_FILENAME = "metadata.yaml";

    private static final Logger logger = LoggerFactory.getLogger(JujuTransformer.class);

    private static final String DEFAULT_ENV_VAR_CONNECTION = "_HOSTNAME";
    private static final String DEFAULT_STARTING_LAYER = "basic";
    private static final String DEFAULT_TARGET_LOCATION = "localhost";
    private static final List<String> HOOK_PLACEHOLDERS = Arrays.asList(
        "config-changed",
        "leader-elected",
        "leader-settings-changed",
        "start",
        "stop",
        "upgrade-charm",
        "update-status"
    );

    private final TransformationContext context;
    private final Configuration cfg = TemplateHelper.forClasspath(JujuPlugin.class, "/plugins/juju");

    public JujuTransformer(TransformationContext context) {
        this.context = context;
    }

    public void generateCharms() throws IOException {
        // Packaging each component stack in a different charm
        for (Graph<RootComponent, RootRelation> stack : context.getModel().findComponentStacks()) {
            // Setting charm name to the name of the base compute node of "stack"
            String charmName = stack.vertexSet()
                .stream()
                .filter(v -> v instanceof Compute)
                .findFirst()
                .get()
                .getNormalizedName();
            logger.info("*** Charming application stack '{}' ***", charmName);

            // Retrieving environment variables, artifacts and operations allowing to provision the nodes in "stack"
            Iterator<RootComponent> nodes = topologicalSort(stack);
            Map<String, String> envVars = new HashMap<String, String>();
            List<Artifact> artifacts = new ArrayList<Artifact>();
            Map<String, List<Operation>> ops = new HashMap<String, List<Operation>>();
            while (nodes.hasNext()) {
                RootComponent node = nodes.next();
                if (node instanceof Compute)
                    logger.info("Skipping deployment information for (Compute) component '{}'", node.getName());
                else {
                    // Retrieving envVars for "node"
                    logger.info("Retrieving environment variables to be set for component '{}'", node.getName());
                    Map<String, Property> props = node.getProperties();
                    for (String propName : props.keySet()) {
                        Property p = props.get(propName);
                        envVars.put(node.getName().concat("_").concat(p.getName()).toUpperCase(), p.getValue());
                    }
                    for (RootRelation relation : node.getRelations())
                        if (relation instanceof ConnectsTo)
                            envVars.put(relation.getTarget().concat(DEFAULT_ENV_VAR_CONNECTION).toUpperCase(),
                                DEFAULT_TARGET_LOCATION);
                    // Retrieving management operations for "node"
                    logger.info("Retrieving artifacts associated with component '{}'", node.getName());
                    node.getArtifacts().stream().forEach(artifacts::add);
                    // Retrieving management operations for "node"
                    logger.info("Retrieving management operations for component '{}'", node.getName());
                    node.getOperations().keySet().forEach(opName -> {
                        if (node.getOperation(opName).isPresent()) {
                            Operation op = node.getOperation(opName).get();
                            if (!ops.containsKey(op.getName()))
                                ops.put(op.getName(), new ArrayList<Operation>());
                            if (op.hasArtifacts())
                                ops.get(opName).add(op);
                        }
                    });
                }
            }

            // Generating charm for "stack"
            generateCharm(charmName, envVars, artifacts, ops);
        }
    }

    /* Method for obtaining an iterator visiting a "stack" graph in topological order
     * - Workaround needed since "TopologicalOrderIterator" was ignoring horizontal relationships
     *   after running "FindComponentStacks"
     */
    private Iterator<RootComponent> topologicalSort(Graph<RootComponent, RootRelation> stack) {
        List<RootComponent> sortedNodes = new ArrayList<RootComponent>();
        List<RootComponent> unsortedNodes = new ArrayList<RootComponent>();
        for (RootComponent node : stack.vertexSet()) {
            unsortedNodes.add(node);
        }
        while (!(unsortedNodes.isEmpty())) {
            Iterator<RootComponent> nodes = unsortedNodes.iterator();
            while (nodes.hasNext()) {
                RootComponent node = nodes.next();
                boolean dependenciesSatisfied = true;
                for (RootRelation dependency : node.getRelations()) {
                    RootComponent target = context.getModel().getComponent(dependency.getTarget()).get();
                    if (stack.vertexSet().contains(target) && !sortedNodes.contains(target))
                        dependenciesSatisfied = false;
                }
                if (dependenciesSatisfied)
                    sortedNodes.add(node);
            }
            unsortedNodes.removeAll(sortedNodes);
        }
        return sortedNodes.iterator();
    }

    /* Method for generating a to-be-compiled charm folder, given
     * - the name of the charm
     * - the environment variables to set, and
     * - the operations to execute in its "install" hooks
     */
    private void generateCharm(String name,
                               Map<String, String> variables,
                               List<Artifact> artifacts,
                               Map<String, List<Operation>> operations) throws IOException {
        logger.info("Preparing charm folder 'layer-{}'", name);

        // Setting relative path to target folder
        String charmFolder = CHARM_FOLDER_PREAMBLE + name;

        // Copying artifacts
        logger.info("Copying artifacts");
        for (String opName : operations.keySet())
            for (Operation op : operations.get(opName))
                artifacts.addAll(op.getArtifacts());
        for (Artifact a : artifacts) {
            String targetPath = Paths.get(charmFolder, a.getValue()).normalize().toString();
            // Forcing artifacts to reside in charmFolder
            if (!targetPath.startsWith(charmFolder))
                targetPath = Paths.get(charmFolder, targetPath).normalize().toString();
            context.getFileAccess().copy(a.getValue(), targetPath);
        }

        // Generating layer file
        logger.info("Generating file 'layer.yaml'");
        generateLayerFile(charmFolder);

        // Generating metadata
        logger.info("Generating file 'metadata.yml'");
        generateMetadataFile(name, charmFolder);

        // Generating hooks
        logger.info("Generating hooks");
        String hooksFolder = Paths.get(charmFolder, HOOKS_FOLDER).normalize().toString();
        generateDefaultHooks(hooksFolder);
        generateInstallHook(variables, operations, hooksFolder);
    }

    /* Method for generating the "metadata.yaml" file required by each charm, given
     * - the "charmFolder".
     */
    private void generateLayerFile(String charmFolder) throws IOException {
        // Setting path of target file
        String layerPath = Paths.get(charmFolder, LAYER_FILENAME).normalize().toString();
        // Building target file from template "layer.yaml"
        Template layer = cfg.getTemplate("layer.yaml");
        Map<String, Object> layerData = new HashMap<String, Object>();
        layerData.put("layer", DEFAULT_STARTING_LAYER);
        context.getFileAccess().append(layerPath, TemplateHelper.toString(layer, layerData));
    }

    /* Method for generating the "metadata.yaml" file required by each charm, given
     * - the charm "name", and
     * - the "charmFolder".
     */
    private void generateMetadataFile(String name, String charmFolder) throws IOException {
        // Setting path of target file
        String metaPath = Paths.get(charmFolder, METADATA_FILENAME).normalize().toString();
        // Building target file from template "layer.yaml"
        Template meta = cfg.getTemplate("metadata.yaml");
        Map<String, Object> metaData = new HashMap<String, Object>();
        metaData.put("name", name);
        metaData.put("series", CHARM_SERIES);
        context.getFileAccess().append(metaPath, TemplateHelper.toString(meta, metaData));
    }

    /* Method for generating placeholder hooks, given
     * - the "hooksFolder".
     */
    private void generateDefaultHooks(String hooksFolder) throws IOException {
        for (String hook : HOOK_PLACEHOLDERS) {
            // Setting path to target hook
            String hookFile = Paths.get(hooksFolder, hook).normalize().toString();
            // Building hook as empty script
            context.getFileAccess().append(hookFile, "#!/bin/bash");
            context.getFileAccess().append(hookFile, "exit 0");
        }
    }

    /* Method for generating the "install" hook, given
     * - the environment "variables" to set,
     * - the "operations" to execute (in the given order), and
     * - the "hooksFolder".
     */
    private void generateInstallHook(
        Map<String, String> variables,
        Map<String, List<Operation>> operations,
        String hooksFolder) throws IOException {
        // Setting path to target file
        String installFile = Paths.get(hooksFolder, HOOK_INSTALL).normalize().toString();
        // Listing environment variables to be exported
        List<EnvironmentVariable> vars = new ArrayList<EnvironmentVariable>();
        for (String varName : variables.keySet())
            vars.add(EnvironmentVariable.builder().name(varName).value(variables.get(varName)).build());
        // Listing scripts to be executed
        // (following standard lifecycle - install, configure, start)
        List<String> commands = new ArrayList<String>();
        List<Operation> ops = operations.get("create");
        ops.addAll(operations.get("configure"));
        ops.addAll(operations.get("start"));
        for (Operation o : ops) {
            for (Artifact a : o.getArtifacts()) {
                // TODO add support for other artifacts than scripts
                String scriptPath = a.getValue();
                // Adapting path to the fact that artifacts are forced to reside in charm folder
                scriptPath = scriptPath.replace("../", "").replace("..\\", "");
                commands.add("/bin/bash " + scriptPath);
            }
        }
        // Building install hook from template "install"
        Template install = cfg.getTemplate(("install"));
        Map<String, Object> installData = new HashMap<String, Object>();
        installData.put("vars", vars);
        installData.put("commands", commands);
        context.getFileAccess().append(installFile, TemplateHelper.toString(install, installData));
    }
}
