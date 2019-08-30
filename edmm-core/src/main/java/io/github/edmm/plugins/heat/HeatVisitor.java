package io.github.edmm.plugins.heat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;
import io.github.edmm.plugins.heat.model.Parameter;
import io.github.edmm.plugins.heat.model.PropertyGetParam;
import io.github.edmm.plugins.heat.model.PropertyGetResource;
import io.github.edmm.plugins.heat.model.PropertyObject;
import io.github.edmm.plugins.heat.model.PropertyValue;
import io.github.edmm.plugins.heat.model.Resource;
import io.github.edmm.plugins.heat.model.Template;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeatVisitor implements ComponentVisitor, RelationVisitor {

    private static final Logger logger = LoggerFactory.getLogger(HeatVisitor.class);

    protected final TransformationContext context;
    protected final Graph<RootComponent, RootRelation> graph;

    public HeatVisitor(TransformationContext context) {
        this.context = context;
        this.graph = context.getTopologyGraph();
    }

    public void populateHeatTemplate() {
        PluginFileAccess fileAccess = context.getFileAccess();

        // TODO
        Template template = new Template();
        template.setName(context.getModel().getName());
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

        try {
            fileAccess.append(template.getName(), template.toYaml());
        } catch (IOException e) {
            logger.error("Failed to write Terraform file", e);
            throw new TransformationException(e);
        }
    }
}
