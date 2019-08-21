package io.github.edmm.plugins.ansible;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class AnsibleLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(AnsibleLifecycle.class);

    private final TransformationContext context;

    public AnsibleLifecycle(TransformationContext context) {
        this.context = context;
    }

    private Configuration cfg;

    @Override
    public void prepare() {
        logger.info("Prepare transformation for Ansible...");
        cfg = TemplateHelper.fromClasspath(new ClassPathResource("plugins/ansible"));
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Ansible...");
        // TODO

        PluginFileAccess fileAccess = context.getFileAccess();
        try {
            // TODO
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Transformation to Ansible successful");
    }

    @Override
    public void cleanup() {
        logger.info("Cleanup transformation leftovers...");
    }
}
