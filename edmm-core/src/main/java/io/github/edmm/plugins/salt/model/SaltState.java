package io.github.edmm.plugins.salt.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.edmm.core.plugin.TemplateHelper;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@Builder
/**
 * Abstraction of a salt state
 */
public class SaltState {
    private static final Logger logger = LoggerFactory.getLogger(SaltState.class);
    public final String id;
    public final String state;
    public final String fun;
    public final Map<String, String> vars;
    public final boolean require;
    public final SaltState requireState;

    /**
     * Convert state into yaml file
     *
     * @param cfg config for template
     * @return yaml formatted state
     */
    public String toString(Configuration cfg) {
        try {
            Template baseTemplate = cfg.getTemplate("state.sls");

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("state", this);
            return TemplateHelper.toString(baseTemplate, templateData);
        } catch (IOException e) {
            logger.error("Failed to write Salt file: {}", e.getMessage(), e);
        }
        return "";
    }
}
