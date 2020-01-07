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

    }

    public void addCompute(Compute compute) {
        this.policy.getModVars().putIfAbsent(compute.getNormalizedName() + "_ip", "10.0.0." + (last_ip++));
        this.policy.getEnvVars().putIfAbsent(compute.getNormalizedName() + "_env", new ArrayList<>());
        String ipVar = "$(" + compute.getNormalizedName() + "_ip)";
        this.policy.getClasses().putIfAbsent(compute.getNormalizedName(), ipVar);

        List<String> methodList = new ArrayList<>();
        this.policy.getMethods().putIfAbsent(compute.getNormalizedName(), methodList);
        methodList.add("copy_files(\"$(deployment_masterfiles)/" + compute.getName() + "\", $(deployment_path))");
    }

    public void add(RootComponent component, Compute compute) {
        handleArtifacts(component, compute);
        handleProperties(component, compute);
        handleOperation(component, compute);
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

    private void handleOperation(RootComponent component, Compute compute) {
        // Create
        if (component.getStandardLifecycle().getCreate().isPresent()) {
            Operation op = component.getStandardLifecycle().getCreate().get();
            if (op.getArtifacts().size() > 0) {
                createCommand(op, component, compute);
            }
        }
        // Configure
        if (component.getStandardLifecycle().getConfigure().isPresent()) {
            Operation op = component.getStandardLifecycle().getConfigure().get();
            if (op.getArtifacts().size() > 0) {
                createCommand(op, component, compute);
            }
        }
        // Start
        if (component.getStandardLifecycle().getStart().isPresent()) {
            Operation op = component.getStandardLifecycle().getStart().get();
            if (op.getArtifacts().size() > 0) {
                createCommand(op, component, compute);
            }
        }
    }

    private void createCommand(Operation operation, RootComponent component, Compute compute) {
        try {
            String[] file = getFileParsed(operation.getArtifacts().get(0).getValue());
            String cfengineFilePath = component.getNormalizedName() + '_' + file[1];
            List<String> methodList = this.policy.getMethods().get(compute.getNormalizedName());
            methodList.add("execute_script($(deployment_path)/" + cfengineFilePath + "\",\n" +
                    "\t\t\t\"" + file[1] + " done.\", $(" + compute.getNormalizedName() + "_env))");

            String localFilePath = DEPLOYMENT_PATH + '/' + compute.getNormalizedName()
                    + '/' + component.getNormalizedName() + '_' + file[1];
            fileAccess.copy(file[0], localFilePath);
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
                            .add(name + "=" + entry.getValue().getValue());
                });
    }

    public void addEnvVar(Compute compute, String var) {
        policy.getEnvVars().get(compute.getNormalizedName() + "_env")
                .add(var);
    }

    public void handleConnectRelation(RootComponent targetComponent, Compute sourceCompute, Compute targetCompute) {
        String name = targetComponent.getNormalizedName().toUpperCase() + "_HOSTNAME";
        List<String> sourceVars = policy.getEnvVars().get(sourceCompute.getNormalizedName() + "_env");
        List<String> targetVars = policy.getEnvVars().get(targetCompute.getNormalizedName() + "_env");
        sourceVars.add(name + "=$(" + targetCompute.getNormalizedName() + "_ip)");
        if (sourceCompute != targetCompute) {
            // Add all the variables of targetComponent + those of the underlying nodes

            //sourceFormula.copyComponentEnvVar(targetFormula, targetComponent);
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
