package io.github.edmm.plugins.heat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.plugins.heat.model.Parameter;
import io.github.edmm.plugins.heat.model.PropertyGetParam;
import io.github.edmm.plugins.heat.model.PropertyGetResource;
import io.github.edmm.plugins.heat.model.PropertyObject;
import io.github.edmm.plugins.heat.model.PropertyValue;
import io.github.edmm.plugins.heat.model.Resource;
import io.github.edmm.plugins.heat.model.Template;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
        resource.addPropertyAssignment("server", new PropertyGetResource("server"));

        List<ImmutablePair<String, String>> pairs = Lists.newArrayList(
                new ImmutablePair<>("name", "foo"),
                new ImmutablePair<>("name", "bar")
        );
        resource.addPropertyAssignment("input", new PropertyObject(pairs));
        Map<String, String> data = new HashMap<>();
        data.put("foo", "fooooo");
        data.put("bar", "baaaaa");
        resource.addPropertyAssignment("input_values", new PropertyObject(data));

        template.addResource(resource);

        System.out.println(template.toYaml());

        logger.info("Transformation to Heat Orchestration Template successful");
    }
}
