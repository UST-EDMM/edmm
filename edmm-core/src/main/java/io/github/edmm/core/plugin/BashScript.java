package io.github.edmm.core.plugin;

import java.io.FileNotFoundException;
import java.io.IOException;

import io.github.edmm.core.transformation.TransformationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BashScript {

    public static final String SHEBANG = "#!/bin/bash";
    public static final String IMMEDIATE_EXIT = "set -e";

    private static final Logger logger = LoggerFactory.getLogger(BashScript.class);

    private PluginFileAccess fileAccess;
    private String scriptPath;

    public BashScript(PluginFileAccess fileAccess, String scriptPath) {
        this.fileAccess = fileAccess;
        this.scriptPath = scriptPath;
        if (!this.scriptPath.endsWith(".sh")) {
            this.scriptPath = this.scriptPath + ".sh";
        }
        this.init();
    }

    private void init() {
        logger.info("Creating bash script at '{}'", scriptPath);
        fileAccess.delete(scriptPath);
        try {
            fileAccess.append(scriptPath, SHEBANG);
            fileAccess.append(scriptPath, IMMEDIATE_EXIT);
        } catch (IOException e) {
            logger.error("Failed to initialize bash script: {}", e.getMessage(), e);
            throw new TransformationException(e);
        }
    }

    public void append(String data) {
        try {
            fileAccess.append(scriptPath, data);
        } catch (IOException e) {
            logger.error("Failed to append to bash script: {}", e.getMessage(), e);
            throw new TransformationException(e);
        }
    }

    public String getScriptPath() throws FileNotFoundException {
        return fileAccess.getAbsolutePath(scriptPath);
    }
}
