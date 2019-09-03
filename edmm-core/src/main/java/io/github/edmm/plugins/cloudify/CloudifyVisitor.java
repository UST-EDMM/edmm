package io.github.edmm.plugins.cloudify;

import freemarker.template.Configuration;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;
import org.jgrapht.Graph;

public abstract class CloudifyVisitor implements ComponentVisitor, RelationVisitor {

    protected final TransformationContext context;
    protected final Configuration cfg = TemplateHelper.forClasspath(CloudifyPlugin.class, "/plugins/cloudify");
    protected final Graph<RootComponent, RootRelation> graph;

    public CloudifyVisitor(TransformationContext context) {
        this.context = context;
        this.graph = context.getTopologyGraph();
    }

    public abstract void populateCloudifyFile();
}
