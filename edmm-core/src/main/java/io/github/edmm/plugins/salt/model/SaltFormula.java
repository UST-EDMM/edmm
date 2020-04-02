package io.github.edmm.plugins.salt.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import io.github.edmm.core.plugin.BashScript;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.model.Operation;
import io.github.edmm.model.component.RootComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that abstracts the configuration of the formula files includes all the commands to copy files to the minions
 * and to execute the operations
 */
public class SaltFormula {
    private static final Logger logger = LoggerFactory.getLogger(SaltFormula.class);
    public final String name;
    public final String hostName;
    private final String DEST_DIR = "/application";
    private final String ENV_FILE = "env.sh";
    private final String BASE_DIR_SALT = "salt";
    private final PluginFileAccess fileAccess;
    private final Configuration cfg;
    private final List<SaltState> states = new ArrayList<>();

    // Associate the component with an increasing insertion index
    private final Map<RootComponent, Integer> componentIndex = new HashMap<>();
    // Mnion environment variables
    private final Map<String, ComponentEnvVar> environmentVars = new HashMap<>();
    // External environment variables
    private final Map<String, ComponentEnvVar> otherVars = new HashMap<>();
    private int componentMapIndex = 0;
    //private final SaltBasePillar pillar;

    private BashScript envScript;

    /**
     * Constructor
     *
     * @param name       minion name
     * @param hostName   minion hostname
     * @param fileAccess fileAccess plugin
     * @param cfg        configuration for state.sls template
     */
    public SaltFormula(String name, String hostName, PluginFileAccess fileAccess, Configuration cfg) {
        this.name = name;
        this.hostName = hostName;
        this.cfg = cfg;
        this.fileAccess = fileAccess;
        // Copy all application files to minion
        copyFiles();
        configureEnvVars();
    }

    /**
     * Save formula to disk
     */
    public void saveFile() {
        StringBuilder sb = new StringBuilder();
        // Save the states to the file
        states.forEach(state -> sb.append(state.toString(cfg)));
        try {
            fileAccess.append(BASE_DIR_SALT + '/' + name + ".sls", sb.toString());
        } catch (IOException e) {
            logger.error("Failed to write Salt file: {}", e.getMessage(), e);
        }
        // Extract all the variables and save them in the file
        environmentVars.putAll(otherVars);
        environmentVars.entrySet().stream()
            .sorted(Comparator.comparingInt(p -> p.getValue().insertIndex))
            .forEach((entry) -> saveEnvVar(entry.getKey(), entry.getValue().getValue()));
    }

    /**
     * Add the component to the formula and execute routines to copy artifacts, import properties and copy operation
     * scripts
     *
     * @param component EDMM component
     */
    public void add(RootComponent component) {
        componentIndex.putIfAbsent(component, componentMapIndex);
        handleArtifacts(component);
        handleProperties(component);
        handleOperation(component);
        componentMapIndex++;
    }

    /**
     * For each operation creates the states to run the scripts
     */
    private void handleOperation(RootComponent component) {
        // Create
        if (component.getStandardLifecycle().getCreate().isPresent()) {
            Operation create = component.getStandardLifecycle().getCreate().get();
            addCmdRunState(create, component);
        }
        // Configure
        if (component.getStandardLifecycle().getConfigure().isPresent()) {
            Operation configure = component.getStandardLifecycle().getConfigure().get();
            addCmdRunState(configure, component);
        }
        // Start
        if (component.getStandardLifecycle().getStart().isPresent()) {
            Operation start = component.getStandardLifecycle().getStart().get();
            addCmdRunState(start, component);
        }
    }

    /**
     * Build the state fo running scripts
     *
     * @param filepath    path script on the minion
     * @param id          id to identify the state
     * @param requireLast if state execution require last state execution
     * @return salt state
     */
    private SaltState buildCmdRunState(String filepath, String id, Boolean requireLast) {
        Map<String, String> vars = new HashMap<>();
        vars.put("name", DEST_DIR + "/" + filepath);
        vars.put("cwd", DEST_DIR);
        vars.put("runas", "root");
        return SaltState.builder()
            .id(id)
            .state("cmd")
            .fun("run")
            .vars(vars)
            .require(requireLast)
            .requireState(requireLast ? states.get(states.size() - 1) : null)
            .build();
    }

    /**
     * Extract the script from the operation and create a state to execute it
     *
     * @param operation operation
     * @param component component for this operation
     */
    private void addCmdRunState(Operation operation, RootComponent component) {
        if (operation.getArtifacts().size() > 0) {
            String[] file = getFileParsed(operation.getArtifacts().get(0).getValue());
            String normalizedName = operation.getArtifacts().get(0).getNormalizedValue();
            try {
                String localFilePath = BASE_DIR_SALT + '/' + name + '/' + component.getNormalizedName() + '_' + file[1];
                fileAccess.copy(file[0], localFilePath);
                String saltFilePath = component.getNormalizedName() + '_' + file[1];
                states.add(buildCmdRunState(saltFilePath, normalizedName, true));
            } catch (IOException e) {
                logger.error("Failed to write Salt file: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * For each artifact copy the files to salt directory
     *
     * @param component EDMM component
     */
    private void handleArtifacts(RootComponent component) {
        component.getArtifacts().forEach(artifact -> {
            String[] file = getFileParsed(artifact.getValue());
            try {
                fileAccess.copy(file[0], BASE_DIR_SALT + '/' + name + '/' + file[1]);
            } catch (IOException e) {
                logger.error("Failed to write Salt file: {}", e.getMessage(), e);
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
     * Add the state to copy all files to minion
     */
    private void copyFiles() {
        Map<String, String> vars = new HashMap<>();
        vars.put("name", DEST_DIR);
        vars.put("source", "salt://" + name);
        vars.put("makedirs", "True");
        vars.put("replace", "True");
        vars.put("clean", "True");
        vars.put("user", "root");
        vars.put("file_mode", "455");
        SaltState state = SaltState.builder()
            .id("copy_files")
            .state("file")
            .fun("recurse")
            .vars(vars)
            .require(false)
            .build();
        states.add(state);
    }

    /**
     * For each property adds the env variable to environmentVars
     *
     * @param component EDMM component
     */
    private void handleProperties(RootComponent component) {
        String[] blacklist = {"key_name", "public_key"};
        component.getProperties().entrySet().stream()
            .filter(entry -> !Arrays.asList(blacklist).contains(entry.getKey()))
            .forEach(entry -> {
                String name = component.getNormalizedName().toUpperCase() + '_' + entry.getKey().toUpperCase();
                ComponentEnvVar var = ComponentEnvVar.builder()
                    .name(name)
                    .value(entry.getValue().getValue())
                    .insertIndex(componentMapIndex).build();
                environmentVars.put(name, var);
            });
    }

    /**
     * Configure environment script file
     */
    private void configureEnvVars() {
        envScript = new BashScript(fileAccess, BASE_DIR_SALT + '/' + name + '/' + name + '_' + ENV_FILE);
        states.add(buildCmdRunState(name + '_' + ENV_FILE,
            "configure_environment", true));
    }

    /**
     * Add an env var to otherVars
     *
     * @param name  variable name
     * @param value variable value
     */
    public void addEnvVar(String name, String value) {
        ComponentEnvVar var = ComponentEnvVar.builder()
            .name(name)
            .value(value)
            .insertIndex(componentMapIndex++).build();
        this.otherVars.putIfAbsent(name, var);
    }

    /**
     * Save env var to file
     *
     * @param name  variable name
     * @param value variable value
     */
    private void saveEnvVar(String name, String value) {
        envScript.append("echo " + name + "=" + value + " >> /etc/environment");
    }

    /**
     * Copy env var of a component to this formula
     *
     * @param from          from formula
     * @param fromComponent component
     */
    public void copyComponentEnvVar(SaltFormula from, RootComponent fromComponent) {
        Integer index = from.componentIndex.get(fromComponent);
        if (index == null) return;
        from.environmentVars.entrySet().stream()
            .filter(entry -> entry.getValue().insertIndex <= index && entry.getValue().insertIndex != 0)
            .sorted(Comparator.comparingInt(p -> p.getValue().insertIndex))
            .forEach(entry -> {
                ComponentEnvVar var = ComponentEnvVar.builder()
                    .name(entry.getKey())
                    .value(entry.getValue().value)
                    .insertIndex(componentMapIndex++).build();
                this.otherVars.putIfAbsent(entry.getKey(), var);
            });
    }
}
