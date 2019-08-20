package io.github.edmm.plugins.terraform;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.plugin.TemplateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class TerraformLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(TerraformLifecycle.class);

    private final TransformationContext context;

    public TerraformLifecycle(TransformationContext context) {
        this.context = context;
    }

    private Configuration cfg;

    @Override
    public void prepare() {
        logger.info("Prepare transformation for Terraform...");
        cfg = TemplateHelper.fromClasspath(new ClassPathResource("plugins/terraform"));
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Terraform...");
        // TODO

        PluginFileAccess fileAccess = context.getFileAccess();
        try {
            Template t = cfg.getTemplate("aws_base.tf");
            fileAccess.append("aws.tf", TemplateHelper.toString(t, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Transformation to Terraform successful");
    }

    @Override
    public void cleanup() {
        logger.info("Cleanup transformation leftovers...");
        // noop
    }
}
