package io.github.edmm.core.plugin;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BashScript {

    public static final String SHEBANG = "#!/bin/bash";
    public static final String SOURCE_ALL_UTILS = "for file in $(ls util); do source util/$file; done";
    public static final String IMMEDIATE_EXIT = "set -e";

    private static final Logger logger = LoggerFactory.getLogger(BashScript.class);

    private PluginFileAccess fileAccess;
    private String scriptPath;

    public BashScript(PluginFileAccess fileAccess, String name) {
        this.fileAccess = fileAccess;
        this.scriptPath = "scripts" + name + ".sh";
        this.init();
    }

    private void init() {
        logger.info("Creating bash script at '{}'", scriptPath);
        fileAccess.delete(scriptPath);
        try {
            fileAccess.append(scriptPath, SHEBANG);
            fileAccess.append(scriptPath, SOURCE_ALL_UTILS);
            fileAccess.append(scriptPath, IMMEDIATE_EXIT);
        } catch (IOException e) {
            logger.error("Failed to initialize bash script: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to initialize bash script");
        }
    }

    public void append(String data) throws IOException {
        fileAccess.append(scriptPath, data);
    }

    public String getScriptPath() throws FileNotFoundException {
        return fileAccess.getAbsolutePath(scriptPath);
    }
}
