package io.github.edmm.plugins.cfengine.model;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.model.Operation;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.plugins.salt.model.SaltFormula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CFAgent {
    private static final Logger logger = LoggerFactory.getLogger(SaltFormula.class);
    public final CFPolicy policy;
    private final PluginFileAccess fileAccess;
    private final String DEPLOYMENT_PATH = "/deployment";
    private final String DEPLOYMENT_NAME = "deployment.cf";
    private final String DEPLOYMENT_MASTERFILES = "/var/cfengine/masterfiles/deployment";
    private final Map<String, List<RootComponent>> runningOrder; //List<RootComponent> runningOrder

    private int last_ip = 0;

    public CFAgent(PluginFileAccess fileAccess) {
        this.fileAccess = fileAccess;
        this.policy = CFPolicy.builder()
                .modVars(new LinkedHashMap<>())
                .envVars(new LinkedHashMap<>())
                .classes(new LinkedHashMap<>())
                .methods(new LinkedHashMap<>())
                .build();
        this.policy.getModVars().putIfAbsent("deployment_path", DEPLOYMENT_PATH);
        this.policy.getModVars().putIfAbsent("deployment_masterfiles", DEPLOYMENT_MASTERFILES);
        this.runningOrder = new HashMap<>();

    }

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

    public void add(RootComponent component, Compute compute) {
        runningOrder.get(compute.getNormalizedName()).add(component);
        handleArtifacts(component, compute);
        handleProperties(component, compute);
        handleOperations(component, compute);
    }

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

    private String[] getFileParsed(String filePath) {
        String file = filePath;
        if (file.startsWith("./")) {
            file = file.substring(2);
        }
        String name = new File(file).getName();
        return new String[]{file, name};
    }

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

    private void createCommand(Operation operation, RootComponent component, Compute compute) {
        try {
            if (operation.getArtifacts().size() > 0) {
                String[] file = getFileParsed(operation.getArtifacts().get(0).getValue());
                String cfengineFilePath = component.getNormalizedName() + '_' + file[1];
                List<String> methodList = this.policy.getMethods().get(compute.getNormalizedName());
                methodList.add("execute_script($(deployment_path)/" + cfengineFilePath + "\",\n" +
                        "\t\t\t\t\"" + file[1] + " done.\", $(" + compute.getNormalizedName() + "_env))");

                String localFilePath = DEPLOYMENT_PATH + '/' + compute.getNormalizedName()
                        + '/' + component.getNormalizedName() + '_' + file[1];
                fileAccess.copy(file[0], localFilePath);
            }
        } catch (IOException e) {
            logger.error("Failed to write CFEngine file: {}", e.getMessage(), e);
        }
    }

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


    public void handleConnectRelation(RootComponent targetComponent, Compute sourceCompute, Compute targetCompute) {
        String name = targetComponent.getNormalizedName().toUpperCase() + "_HOSTNAME";
        Map<String, String> sourceVars = policy.getEnvVars().get(sourceCompute.getNormalizedName() + "_env");
        Map<String, String> targetVars = policy.getEnvVars().get(targetCompute.getNormalizedName() + "_env");
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

    public String saveFile(Configuration cfg) {
        try {
            Template baseTemplate = cfg.getTemplate("policy.cf");

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("agent", policy);
            fileAccess.append(DEPLOYMENT_PATH + '/' + DEPLOYMENT_NAME, TemplateHelper.toString(baseTemplate, templateData));
            return TemplateHelper.toString(baseTemplate, templateData);
        } catch (IOException e) {
            logger.error("Failed to write CFengine file: {}", e.getMessage(), e);
        }
        return "";
    }
}
