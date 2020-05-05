package io.github.edmm.plugins.cfengine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.TemplateHelper;
import io.github.edmm.core.TopologyGraphHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.Operation;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.MysqlDatabase;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.Tomcat;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;
import io.github.edmm.model.visitor.VisitorHelper;
import io.github.edmm.plugins.cfengine.model.CFPolicy;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.jgrapht.Graph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CFEngineTransformer implements ComponentVisitor, RelationVisitor {

    private static final Logger logger = LoggerFactory.getLogger(CFEngineTransformer.class);

    private static final String DEPLOYMENT_PATH = "/deployment";
    private static final String DEPLOYMENT_NAME = "deployment.cf";
    private static final String DEPLOYMENT_MASTERFILES = "/var/cfengine/masterfiles/deployment";

    public final CFPolicy policy;

    private final TransformationContext context;
    private final Configuration cfg = TemplateHelper.forClasspath(CFEnginePlugin.class, "/plugins/cfengine");
    private final Graph<RootComponent, RootRelation> graph;
    private final PluginFileAccess fileAccess;
    private final Map<String, List<RootComponent>> runningOrder; //List<RootComponent> runningOrder

    private int last_ip = 0;

    public CFEngineTransformer(TransformationContext context) {
        this.context = context;
        this.fileAccess = context.getFileAccess();
        this.graph = context.getTopologyGraph();

        this.policy = CFPolicy.builder()
            .modVars(new LinkedHashMap<>())
            .envVars(new LinkedHashMap<>())
            .classes(new LinkedHashMap<>())
            .methods(new LinkedHashMap<>())
            .build();
        // Template initialization
        this.policy.getModVars().putIfAbsent("deployment_path", DEPLOYMENT_PATH);
        this.policy.getModVars().putIfAbsent("deployment_masterfiles", DEPLOYMENT_MASTERFILES);
        this.runningOrder = new HashMap<>();
    }

    /**
     * Visit all the components in topological order and create relations
     */
    public void visitComponentsTopologicalOrder() {
        // Reverse the graph to find sources
        EdgeReversedGraph<RootComponent, RootRelation> dependencyGraph
            = new EdgeReversedGraph<>(context.getModel().getTopology());
        // Apply the topological sort
        TopologicalOrderIterator<RootComponent, RootRelation> iterator
            = new TopologicalOrderIterator<>(dependencyGraph);
        // Visit all components in topological sort
        while (iterator.hasNext()) {
            RootComponent component = iterator.next();
            logger.info("Generate a state for component " + component.getName());
            component.accept(this);
        }
        // Visit all relations
        VisitorHelper.visit(context.getModel().getRelations(), this);
    }

    /**
     * Populate policy file
     */
    public void populateCFEngineFiles() {
        try {
            Template baseTemplate = cfg.getTemplate("policy.cf");

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("agent", policy);
            fileAccess.append(DEPLOYMENT_PATH + '/' + DEPLOYMENT_NAME, TemplateHelper.toString(baseTemplate, templateData));
        } catch (IOException e) {
            logger.error("Failed to write CFengine file: {}", e.getMessage(), e);
        }
    }

    @Override
    public void visit(Compute component) {
        addCompute(component);
        add(component, component);
        component.setTransformed(true);
    }

    @Override
    public void visit(Tomcat component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
            .orElseThrow(TransformationException::new);
        add(component, compute);
        component.setTransformed(true);
    }

    @Override
    public void visit(MysqlDatabase component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
            .orElseThrow(TransformationException::new);
        add(component, compute);
        component.setTransformed(true);
    }

    @Override
    public void visit(MysqlDbms component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
            .orElseThrow(TransformationException::new);
        add(component, compute);
        component.setTransformed(true);
    }

    @Override
    public void visit(WebApplication component) {
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
            .orElseThrow(TransformationException::new);
        add(component, compute);
        component.setTransformed(true);
    }

    @Override
    public void visit(ConnectsTo relation) {
        RootComponent sourceComponent = graph.getEdgeSource(relation);
        RootComponent targetComponent = graph.getEdgeTarget(relation);
        Optional<Compute> optionalSourceCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, sourceComponent);
        Optional<Compute> optionalTargetCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, targetComponent);
        // If there are compute nodes
        if (optionalSourceCompute.isPresent() && optionalTargetCompute.isPresent()) {
            handleConnectRelation(targetComponent, optionalSourceCompute.get(), optionalTargetCompute.get());
        }
    }

    /**
     * Add env vars to the target host to enable the connection
     */
    public void handleConnectRelation(RootComponent targetComponent, Compute sourceCompute, Compute targetCompute) {
        String name = targetComponent.getNormalizedName().toUpperCase() + "_HOSTNAME";
        Map<String, String> sourceVars = policy.getEnvVars().get(sourceCompute.getNormalizedName() + "_env");
        sourceVars.putIfAbsent(name, "$(" + targetCompute.getNormalizedName() + "_ip)");
        if (sourceCompute != targetCompute) {
            // Add all the variables of targetComponent + those of the underlying nodes
            List<RootComponent> components = runningOrder.get(targetCompute.getNormalizedName());
            Collections.reverse(components);
            boolean found = false;
            for (RootComponent component : components) {
                if (component == targetComponent) found = true;
                if (found && component != targetCompute) {
                    String[] blacklist = {"key_name", "public_key"};
                    component.getProperties().entrySet().stream()
                        .filter(entry -> !Arrays.asList(blacklist).contains(entry.getKey()))
                        .forEach(entry -> {
                            String nameComponent = component.getNormalizedName().toUpperCase() + '_' + entry.getKey().toUpperCase();
                            sourceVars.putIfAbsent(nameComponent, entry.getValue().getValue());
                        });
                }
            }
        }
    }

    /**
     * Create all the variables, classes and methods needed to add a host to the policy
     *
     * @param compute Component to be processed
     */
    public void addCompute(Compute compute) {
        this.policy.getModVars().putIfAbsent(compute.getNormalizedName() + "_ip", "10.0.0." + (last_ip++));
        this.policy.getEnvVars().putIfAbsent(compute.getNormalizedName() + "_env", new LinkedHashMap<>());
        String ipVar = "$(" + compute.getNormalizedName() + "_ip)";
        this.policy.getClasses().putIfAbsent(compute.getNormalizedName(), ipVar);

        List<String> methodList = new ArrayList<>();
        this.policy.getMethods().putIfAbsent(compute.getNormalizedName(), methodList);
        methodList.add("copy_files(\"$(deployment_masterfiles)/" + compute.getName() + "\", $(deployment_path))");
        runningOrder.putIfAbsent(compute.getNormalizedName(), new ArrayList<>());
    }

    /**
     * Add the component to the policy and execute routines to copy artifacts, import properties and copy operation
     * scripts
     *
     * @param component Component to be processed
     */
    public void add(RootComponent component, Compute compute) {
        runningOrder.get(compute.getNormalizedName()).add(component);
        handleArtifacts(component, compute);
        handleProperties(component, compute);
        handleOperations(component, compute);
    }

    /**
     * For each artifact copy the files to CFEngine directory
     *
     * @param component Component to be processed
     * @param compute   Compute node of component
     */
    private void handleArtifacts(RootComponent component, Compute compute) {
        component.getArtifacts().forEach(artifact -> {
            String[] file = getFileParsed(artifact.getValue());
            try {
                fileAccess.copy(file[0], DEPLOYMENT_PATH
                    + '/' + compute.getNormalizedName() + '/' + file[1]);
            } catch (IOException e) {
                logger.error("Failed to write CFEngine file: {}", e.getMessage(), e);
            }
        });
    }

    /**
     * Sanitize filepath
     *
     * @param filePath path
     * @return [filePath, fileName]
     */
    private String[] getFileParsed(String filePath) {
        String file = filePath;
        if (file.startsWith("./")) {
            file = file.substring(2);
        }
        String name = new File(file).getName();
        return new String[] {file, name};
    }

    /**
     * For each operation creates the command to run the scripts
     */
    private void handleOperations(RootComponent component, Compute compute) {
        List<Operation> operations = new ArrayList<>();
        // Create
        if (component.getStandardLifecycle().getCreate().isPresent())
            operations.add(component.getStandardLifecycle().getCreate().get());
        // Configure
        if (component.getStandardLifecycle().getConfigure().isPresent())
            operations.add(component.getStandardLifecycle().getConfigure().get());
        // Start
        if (component.getStandardLifecycle().getStart().isPresent())
            operations.add(component.getStandardLifecycle().getStart().get());
        operations.forEach(operation -> createCommand(operation, component, compute));
    }

    /**
     * Extract the script from the operation and create a command to execute it
     *
     * @param operation operation
     * @param component component for this operation
     */
    private void createCommand(Operation operation, RootComponent component, Compute compute) {
        try {
            if (operation.getArtifacts().size() > 0) {
                String[] file = getFileParsed(operation.getArtifacts().get(0).getValue());
                String cfengineFilePath = component.getNormalizedName() + '_' + file[1];
                List<String> methodList = this.policy.getMethods().get(compute.getNormalizedName());
                methodList.add("execute_script($(deployment_path), \"" + cfengineFilePath + "\",\n" +
                    "\t\t\t\t\"" + cfengineFilePath + "\", $(" + compute.getNormalizedName() + "_env))");

                String localFilePath = DEPLOYMENT_PATH + '/' + compute.getNormalizedName()
                    + '/' + component.getNormalizedName() + '_' + file[1];
                fileAccess.copy(file[0], localFilePath);
            }
        } catch (IOException e) {
            logger.error("Failed to write CFEngine file: {}", e.getMessage(), e);
        }
    }

    /**
     * For each property adds the env variable to policy
     *
     * @param component EDMM component
     */
    private void handleProperties(RootComponent component, Compute compute) {
        String[] blacklist = {"key_name", "public_key"};
        component.getProperties().entrySet().stream()
            .filter(entry -> !Arrays.asList(blacklist).contains(entry.getKey()))
            .forEach(entry -> {
                String name = component.getNormalizedName().toUpperCase() + '_' + entry.getKey().toUpperCase();
                policy.getEnvVars().get(compute.getNormalizedName() + "_env")
                    .putIfAbsent(name, entry.getValue().getValue());
            });
    }
}
