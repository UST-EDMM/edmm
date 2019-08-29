package io.github.edmm.plugins.heat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.plugins.heat.model.Parameter;
import io.github.edmm.plugins.heat.model.PropertyGetParam;
import io.github.edmm.plugins.heat.model.PropertyValue;
import io.github.edmm.plugins.heat.model.Resource;
import io.github.edmm.plugins.heat.model.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeatLifecycle extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(HeatLifecycle.class);

    private final TransformationContext context;

    public HeatLifecycle(TransformationContext context) {
        this.context = context;
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Heat Orchestration Template...");

        Template template = new Template();
        template.addParameter(Parameter.builder().name("foo").type("string").defaultValue("foo").build());
        template.addParameter(Parameter.builder().name("bar").type("string").defaultValue("bar").build());

        Resource resource = Resource.builder().name("server").type("foo").build();
        resource.addPropertyAssignment("image", new PropertyValue("baz"));
        resource.addPropertyAssignment("flavor", new PropertyGetParam("bar"));
        template.addResource(resource);

        System.out.println(template.toYaml());

        logger.info("Transformation to Heat Orchestration Template successful");
    }
}
