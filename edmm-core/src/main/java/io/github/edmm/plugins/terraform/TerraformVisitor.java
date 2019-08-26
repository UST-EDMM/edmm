package io.github.edmm.plugins.terraform;

import freemarker.template.Configuration;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;
import org.jgrapht.Graph;
import org.springframework.core.io.ClassPathResource;

public abstract class TerraformVisitor implements ComponentVisitor, RelationVisitor {

    protected final TransformationContext context;
    protected final Configuration cfg = TemplateHelper.fromClasspath(new ClassPathResource("plugins/terraform"));
    protected final Graph<RootComponent, RootRelation> graph;

    public TerraformVisitor(TransformationContext context) {
        this.context = context;
        this.graph = context.getTopologyGraph();
    }

    public abstract void populateTerraformFile();
}
