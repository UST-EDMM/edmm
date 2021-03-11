package io.github.edmm.plugins.ansible;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import io.github.edmm.core.TemplateHelper;
import io.github.edmm.core.TopologyGraphHelper;
import io.github.edmm.core.TransformationHelper;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.Artifact;
import io.github.edmm.model.Operation;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;
import io.github.edmm.plugins.ansible.model.AnsibleFile;
import io.github.edmm.plugins.ansible.model.AnsibleHost;
import io.github.edmm.plugins.ansible.model.AnsiblePlay;
import io.github.edmm.plugins.ansible.model.AnsibleTask;
import io.github.edmm.plugins.salt.IpGenerator;
import io.github.edmm.utils.Consts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import freemarker.template.Configuration;
import lombok.var;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.model.component.Compute.PUBLIC_ADDRESS;
import static io.github.edmm.plugins.ansible.AnsibleLifecycle.FILE_NAME;

public class AnsibleVisitor implements ComponentVisitor, RelationVisitor {

    private static final Logger logger = LoggerFactory.getLogger(AnsibleVisitor.class);

    protected final TransformationContext context;
    protected final Configuration cfg = TemplateHelper.forClasspath(AnsibleVisitor.class, "/plugins/ansible");
    protected final Graph<RootComponent, RootRelation> graph;

    private final Map<String, AnsibleHost> hosts = new HashMap<>();
    private final List<AnsiblePlay> plays = new ArrayList<>();

    private final IpGenerator gen = new IpGenerator();
    private final Map<Compute, List<RootComponent>> stackMapping = new HashMap<>();

    public AnsibleVisitor(TransformationContext context) {
        this.context = context;
        this.graph = context.getTopologyGraph();
    }

    @Override
    public void visit(RootComponent component) {
        logger.info("Generate a play for component {}", component.getName());

        copyFiles(component);
        Map<String, String> envVars = TransformationHelper.collectEnvVars(graph, component);
        // List<String> runtimeEnvVars = TransformationHelper.collectRuntimeEnvVars(graph, component);
        List<AnsibleTask> tasks = convertOperations(collectOperations(component), component);
        List<AnsibleFile> files = collectFiles(component);

        // Determine compute host
        Compute compute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component)
            .orElseThrow(() -> new IllegalStateException(String.format("Component %s doesn't have a host", component.getName())));
        String host = compute.getNormalizedName();

        // Determine path to SSH key file
        String absoluteKeyPath = null;
        Optional<String> optionalPrivateKeyPath = compute.getPrivateKeyPath();
        if (optionalPrivateKeyPath.isPresent()) {
            File file = new File(optionalPrivateKeyPath.get());
            if (file.isFile()) {
                absoluteKeyPath = file.getAbsolutePath();
            }
        }
        if (absoluteKeyPath == null) {
            logger.warn("Could not determine SSH private key file; path in private_key_path property may be wrong");
            absoluteKeyPath = new File(SystemUtils.getUserHome(), ".ssh" + Consts.FS + "id_rsa").getAbsolutePath();
        }

        hosts.put(host, new AnsibleHost(host, absoluteKeyPath));
        AnsiblePlay play = AnsiblePlay.builder()
            .name(component.getName())
            .hosts(host)
            .vars(envVars)
            // .runtimeVars(runtimeEnvVars)
            .tasks(tasks)
            .files(files)
            .build();
        plays.add(play);

        stackMapping.computeIfAbsent(compute, k -> new ArrayList<>());
        stackMapping.get(compute).add(component);

        component.setTransformed(true);
    }

    @Override
    public void visit(Compute component) {
        try {
            component.addProperty(PUBLIC_ADDRESS.getName(), gen.getNextIp());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        component.setTransformed(true);
    }

    private List<AnsibleFile> collectFiles(RootComponent component) {
        List<AnsibleFile> files = new ArrayList<>();
        for (Artifact artifact : component.getArtifacts()) {
            String basename = FilenameUtils.getName(artifact.getValue());
            String newPath = "./files/" + component.getNormalizedName() + "/" + basename;
            String destination = "/opt/" + component.getNormalizedName() + "/";
            files.add(new AnsibleFile(newPath, destination + basename));
        }
        return files;
    }

    private List<AnsibleTask> convertOperations(List<Artifact> operations, RootComponent component) {
        List<AnsibleTask> tasks = new ArrayList<>();
        operations.forEach(operation -> {
            String basename = FilenameUtils.getName(operation.getValue());
            String newPath = "./files/" + component.getNormalizedName() + "/" + basename;
            Map<String, String> args = new HashMap<>();
            // args.put("chdir", "/opt/" + component.getNormalizedName());
            AnsibleTask task = AnsibleTask.builder()
                .name(operation.getNormalizedName())
                .script(newPath)
                .args(args)
                .build();
            tasks.add(task);
        });
        return tasks;
    }

    private void copyFiles(RootComponent component) {
        PluginFileAccess fileAccess = context.getFileAccess();
        // copy referenced artifacts
        for (Artifact artifact : component.getArtifacts()) {
            try {
                String basename = FilenameUtils.getName(artifact.getValue());
                String newPath = "./files/" + component.getNormalizedName() + "/" + basename;
                fileAccess.copy(artifact.getValue(), newPath);
            } catch (IOException e) {
                logger.warn("Failed to copy file from {}", artifact.getValue());
            }
        }
        // copy operation scripts
        for (Artifact artifact : collectOperations(component)) {
            try {
                String basename = FilenameUtils.getName(artifact.getValue());
                String newPath = "./files/" + component.getNormalizedName() + "/" + basename;
                fileAccess.copy(artifact.getValue(), newPath);
            } catch (IOException e) {
                logger.warn("Failed to copy file from {}", artifact.getValue());
            }
        }
    }

    private List<Artifact> collectOperations(RootComponent component) {
        List<Artifact> operations = new ArrayList<>();
        Consumer<Operation> artifactsConsumer = op -> operations.addAll(op.getArtifacts());
        component.getStandardLifecycle().getCreate().ifPresent(artifactsConsumer);
        component.getStandardLifecycle().getConfigure().ifPresent(artifactsConsumer);
        component.getStandardLifecycle().getStart().ifPresent(artifactsConsumer);
        return operations;
    }

    public void populate() {
        PluginFileAccess fileAccess = context.getFileAccess();
        Map<String, Object> data = new HashMap<>();
        data.put("plays", plays);
        data.put("hosts", hosts);
        try {
            fileAccess.write(FILE_NAME, TemplateHelper.toString(cfg, "playbook_base.yml", data));
            for (var entry : stackMapping.entrySet()) {
                Map<String, Property> computedProps = new HashMap<>();
                for (var component : entry.getValue()) {
                    computedProps.putAll(TopologyGraphHelper.resolveComputedProperties(graph, component));
                }
                fileAccess.write(entry.getKey().getName() + "_props.json", convertToJson(computedProps));
            }
        } catch (Exception e) {
            logger.error("Error writing Ansible files: {}", e.getMessage(), e);
        }
    }

    public String convertToJson(Map<String, Property> properties) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        var json = new JsonObject();
        for (var prop : properties.entrySet()) {
            json.addProperty(prop.getKey().toUpperCase(), prop.getValue().getValue());
        }
        var envList = new JsonArray();
        json.add("env", envList);
        for (var prop : properties.entrySet()) {
            var element = new JsonObject();
            element.addProperty("key", prop.getKey().toUpperCase());
            element.addProperty("value", prop.getValue().getValue());
            envList.add(element);
        }
        return gson.toJson(json);
    }
}
