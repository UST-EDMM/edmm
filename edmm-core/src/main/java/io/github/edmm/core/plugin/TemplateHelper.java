package io.github.edmm.core.plugin;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.utils.Consts;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helps with template creation tasks using the freemarker library
 */
public abstract class TemplateHelper {

    private static final Logger logger = LoggerFactory.getLogger(TemplateHelper.class);

    public static Configuration fromFile(File file) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
        try {
            cfg.setDirectoryForTemplateLoading(file);
        } catch (IOException e) {
            logger.error("Failed loading template fragments", e);
            throw new TransformationException("Failed loading template fragments");
        }
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        return cfg;
    }

    public static Configuration forClasspath(Class clazz, String basePath) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
        cfg.setClassForTemplateLoading(clazz, basePath);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        return cfg;
    }

    public static Template getTemplate(Configuration cfg, String name) {
        try {
            return cfg.getTemplate(name);
        } catch (Exception e) {
            logger.error("Failed to load template", e);
            throw new IllegalStateException("Failed to load template");
        }
    }

    public static String toString(Configuration cfg, String name, Map<String, Object> data) {
        return toString(getTemplate(cfg, name), data);
    }

    public static String toString(Template template, Map<String, Object> data) {
        StringWriter sw = new StringWriter();
        try {
            template.process(data, sw);
        } catch (Exception e) {
            logger.error("Failed to write template data", e);
            return Consts.EMPTY;
        }
        return sw.toString();
    }
}
