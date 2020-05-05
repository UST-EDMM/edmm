package io.github.edmm.plugins.salt.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.TemplateHelper;
import io.github.edmm.model.component.Compute;
import io.github.edmm.plugins.salt.SaltStackTransformer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that abstracts the configuration of the top.sls file
 */
public class SaltBase {
    private static final Logger logger = LoggerFactory.getLogger(SaltStackTransformer.class);
    public final PluginFileAccess fileAccess;
    public final Configuration cfg;
    //private final String BASE_DIR_PILLAR = "pillar";
    private final String NAME = "top.sls";
    private final String BASE_DIR_SALT = "salt";
    private final List<SaltMinion> minions = new ArrayList<>();

    public SaltBase(PluginFileAccess fileAccess, Configuration cfg) {
        this.cfg = cfg;
        this.fileAccess = fileAccess;
    }

    /**
     * Add the base state file for the compute node in top.sls
     *
     * @param component compute node
     */
    public void addMinion(Compute component) {
        SaltMinion minion = SaltMinion.builder()
            .name("minion_" + component.getNormalizedName())
            .file(component.getNormalizedName())
            .build();
        minions.add(minion);
    }

    /**
     * Save top.sls file
     */
    public void saveFile() {
        try {
            Template baseTemplate = cfg.getTemplate(NAME);

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("minions", minions);
            fileAccess.append(BASE_DIR_SALT + '/' + NAME, TemplateHelper.toString(baseTemplate, templateData));
            //fileAccess.append(BASE_DIR_PILLAR+'/'+NAME, TemplateHelper.toString(baseTemplate, templateData));

        } catch (IOException e) {
            logger.error("Failed to write Salt file: {}", e.getMessage(), e);
        }
    }
}
