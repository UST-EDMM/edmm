package io.github.edmm.plugins.puppet.typetransformers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import io.github.edmm.core.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.plugins.TransformType;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.plugins.puppet.PuppetLifecycle.MANIFESTS_FOLDER;
import static io.github.edmm.plugins.puppet.PuppetLifecycle.MANIFEST_EXTENSION;
import static io.github.edmm.plugins.puppet.PuppetLifecycle.MANIFEST_MAIN;
import static io.github.edmm.plugins.puppet.PuppetLifecycle.MODULES_FOLDER;

/**
 * This mapping uses the MySQL installation provided on puppet forge:
 * https://forge.puppet.com/modules/puppetlabs/mysql
 */
public class MySQLDBMSonPuppet implements TransformType<String> {

    private static final Logger logger = LoggerFactory.getLogger(MySQLDBMSonPuppet.class);

    private static final String type = "::mysql::server";

    private final TransformationContext context;
    private final Configuration config;

    public MySQLDBMSonPuppet(TransformationContext context, Configuration config) {
        this.context = context;
        this.config = config;
    }

    @Override
    public boolean canHandle(RootComponent component) {
        return component instanceof MysqlDbms;
    }

    @Override
    public String performTransformation(RootComponent component) {
        if (!(component instanceof MysqlDbms)) {
            logger.error("Component {} is not of type MySQL DBMS", component.getName());
            throw new TransformationException("The given component is not of type MySQL DBMS!");
        }

        MysqlDbms dbms = (MysqlDbms) component;

        Map<String, Object> properties = new HashMap<>();
        if (dbms.getRootPassword().isPresent()) {
            properties.put("root_password", dbms.getRootPassword().get());
        }
        if (dbms.getPort().isPresent()) {
            // skipping for now, maybe check out https://forge.puppet.com/modules/puppetlabs/mysql/readme#customize-server-options
            logger.info("Cannot set custom port... Defaulting to 3306.");
        }

        try {
            Template overrideTemplate = this.config.getTemplate("customization_template.pp");

            Map<String, Object> mySql = new HashMap<>();
            mySql.put("component", type);
            mySql.put("properties", properties);

            Path componentManifestsFolder = Paths.get(MODULES_FOLDER, component.getNormalizedName(), MANIFESTS_FOLDER, MANIFEST_MAIN.concat(MANIFEST_EXTENSION));

            this.context.getFileAccess().append(componentManifestsFolder.toString(), TemplateHelper.toString(overrideTemplate, mySql));
        } catch (IOException e) {
            logger.error("Error while generating template.", e);
        }

        return type;
    }
}
