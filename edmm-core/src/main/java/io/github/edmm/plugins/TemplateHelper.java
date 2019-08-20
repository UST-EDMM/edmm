package io.github.edmm.plugins;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.utils.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public abstract class TemplateHelper {

    private static final Logger logger = LoggerFactory.getLogger(TemplateHelper.class);

    public static Configuration fromClasspath(ClassPathResource resource) {
        try {
            return fromFile(resource.getFile());
        } catch (IOException e) {
            logger.error("Failed loading template fragments", e);
            throw new TransformationException("Failed loading template fragments");
        }
    }

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
