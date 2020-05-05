package io.github.edmm.plugins.terraform;

import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;

import freemarker.template.Configuration;
import org.jgrapht.Graph;

public abstract class TerraformVisitor implements ComponentVisitor, RelationVisitor {

    protected final TransformationContext context;
    protected final Configuration cfg = TemplateHelper.forClasspath(TerraformPlugin.class, "/plugins/terraform");
    protected final Graph<RootComponent, RootRelation> graph;

    public TerraformVisitor(TransformationContext context) {
        this.context = context;
        this.graph = context.getTopologyGraph();
    }

    public abstract void populateTerraformFile();
}
