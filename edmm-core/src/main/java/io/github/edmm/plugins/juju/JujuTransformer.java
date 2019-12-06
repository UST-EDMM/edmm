package io.github.edmm.plugins.juju;

import freemarker.template.Configuration;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.plugins.puppet.PuppetPlugin;
import io.github.edmm.plugins.puppet.PuppetTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JujuTransformer {

    private static final Logger logger = LoggerFactory.getLogger(PuppetTransformer.class);

    private final TransformationContext context;
    private final Configuration cfg = TemplateHelper.forClasspath(PuppetPlugin.class, "/plugins/juju");

    public JujuTransformer(TransformationContext context) {
        this.context = context;
    }

    public void generateCharms() {
        // TODO Write generator!!!
        for(RootComponent c : context.getTopologyGraph().vertexSet())
            System.out.println(c.getName() + ":" + c.getType());
    }
}
