package io.github.edmm.plugins.cfn;

import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudFormationVisitor implements ComponentVisitor, RelationVisitor {

    private static final Logger logger = LoggerFactory.getLogger(CloudFormationVisitor.class);

    private final TransformationContext context;
    private final Graph<RootComponent, RootRelation> graph;

    public CloudFormationVisitor(TransformationContext context) {
        this.context = context;
        this.graph = context.getTopologyGraph();
    }

    public void populateFile() {

    }

    @Override
    public void visit(Compute component) {

    }
}
