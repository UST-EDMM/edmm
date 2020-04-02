package io.github.edmm.plugins.cfn;

import java.util.Map;

import com.scaleset.cfbuilder.ec2.metadata.CFNCommand;
import com.scaleset.cfbuilder.ec2.metadata.CFNFile;
import io.github.edmm.core.plugin.BashScript;
import io.github.edmm.core.plugin.PluginFileAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.plugins.cfn.CloudFormationModule.CONFIG_INIT;
import static io.github.edmm.plugins.cfn.CloudFormationModule.CONFIG_SETS;
import static io.github.edmm.plugins.cfn.CloudFormationModule.MODE_777;
import static io.github.edmm.plugins.cfn.CloudFormationModule.OWNER_GROUP_ROOT;

public class EnvHandler {

    private static final Logger logger = LoggerFactory.getLogger(EnvHandler.class);

    private static final String ECHO = "echo ";
    private static final String EXPORT = "export ";
    private static final String REDIRECT_OUTPUT_TO = " >> ";
    private static final String ETC_ENVIRONMENT = "/etc/environment";

    private final CloudFormationModule module;
    private final PluginFileAccess fileAccess;

    public EnvHandler(CloudFormationModule module, PluginFileAccess fileAccess) {
        this.module = module;
        this.fileAccess = fileAccess;
    }

    public void handleEnvVars() {
        Map<String, Map<String, Object>> envVars = this.module.getEnvVars();
        writeEnvScripts(envVars);
        addEnvScripts(envVars);
    }

    private String getFilename(String name) {
        return "set-env-" + name.replace("_", "-") + ".sh";
    }

    private void writeEnvScripts(Map<String, Map<String, Object>> envVars) {
        logger.debug("Writing setEnv scripts.");
        for (Map.Entry<String, Map<String, Object>> computeEntry : envVars.entrySet()) {
            String filename = getFilename(computeEntry.getKey());
            BashScript envScript = new BashScript(fileAccess, filename);
            for (Map.Entry<String, Object> entry : computeEntry.getValue().entrySet()) {
                envScript.append(ECHO + EXPORT + entry.getKey() + "=${" + entry.getKey() + "}" + REDIRECT_OUTPUT_TO + ETC_ENVIRONMENT);
            }
        }
    }

    private void addEnvScripts(Map<String, Map<String, Object>> envVars) {
        for (Map.Entry<String, Map<String, Object>> computeEntry : envVars.entrySet()) {
            String computeName = computeEntry.getKey();
            String filename = getFilename(computeName);
            String name = filename
                .replace("-", "_")
                .replace(".", "_");
            String source = String.format("http://%s.s3.amazonaws.com/%s", module.getBucketName(), filename);
            CFNFile cfnFile = new CFNFile("/opt/env.sh")
                .setSource(source)
                .setMode(MODE_777)
                .setOwner(OWNER_GROUP_ROOT)
                .setGroup(OWNER_GROUP_ROOT);
            CFNCommand cfnCommand = new CFNCommand(name, "/opt/env.sh")
                .setCwd("/opt/");
            for (Map.Entry<String, Object> var : computeEntry.getValue().entrySet()) {
                cfnCommand.addEnv(var.getKey(), var.getValue());
            }
            module.getOperations(computeName).ifPresent(init -> init
                .getOrAddConfig(CONFIG_SETS, CONFIG_INIT)
                .putFile(cfnFile)
                .putCommand(cfnCommand));
        }
    }
}
