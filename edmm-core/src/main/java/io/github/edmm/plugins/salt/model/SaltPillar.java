package io.github.edmm.plugins.salt.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import io.github.edmm.core.plugin.PluginFileAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that abstracts the configuration of the top.sls file of the  pillar
 */
public class SaltPillar {
    private static final Logger logger = LoggerFactory.getLogger(SaltPillar.class);
    private final String BASE_DIR_SALT = "pillar";
    private final String NAME;
    private final PluginFileAccess fileAccess;
    private final Configuration cfg;
    private final Map<String, String> vars = new HashMap<>();

    public SaltPillar(String name, PluginFileAccess fileAccess, Configuration cfg) {
        this.NAME = name;
        this.cfg = cfg;
        this.fileAccess = fileAccess;
    }

    public void saveFile() {
        StringBuilder sb = new StringBuilder();
        vars.forEach((k, v) -> sb.append(k + '=' + v + '\n'));
        try {
            fileAccess.append(BASE_DIR_SALT + '/' + NAME + ".sls", sb.toString());
        } catch (IOException e) {
            logger.error("Failed to write Salt file: {}", e.getMessage(), e);
        }
    }

    public void addVar(String key, String value) {
        vars.put(key, value);
    }
}