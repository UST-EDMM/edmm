package io.github.edmm.plugins.terraform;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.visitor.VisitorHelper;
import io.github.edmm.utils.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class TerraformLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(TerraformLifecycle.class);

    private final TransformationContext context;

    private Configuration cfg;
    private DeploymentModel model;

    public TerraformLifecycle(TransformationContext context) {
        this.context = context;
    }

    @Override
    public void prepare() {
        logger.info("Prepare transformation for Terraform...");
        cfg = TemplateHelper.fromClasspath(new ClassPathResource("plugins/terraform"));
        model = context.getModel();
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Terraform...");

        PluginFileAccess fileAccess = context.getFileAccess();

        ComputeVisitor visitor = new ComputeVisitor(context, cfg);
        VisitorHelper.visit(model.getComponents(), visitor);

        Map<RootComponent, List<String>> content = visitor.getTemplateContent();
        for (Map.Entry<RootComponent, List<String>> entry : content.entrySet()) {
            try {
                fileAccess.append("deploy.tf", String.join(Consts.NL, entry.getValue()));
            } catch (IOException e) {
                logger.error("Failed to write Terraform file: {}", e.getMessage(), e);
            }
        }
        logger.info("Transformation to Terraform successful");
    }

    @Override
    public void cleanup() {
        logger.info("Cleanup transformation leftovers...");
        // noop
    }
}
