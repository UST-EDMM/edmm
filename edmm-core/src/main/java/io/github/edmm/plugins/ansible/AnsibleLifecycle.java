package io.github.edmm.plugins.ansible;

import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.VisitorHelper;

import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnsibleLifecycle extends AbstractLifecycle {

    public static final String FILE_NAME = "deployment.yml";

    private static final Logger logger = LoggerFactory.getLogger(AnsibleLifecycle.class);

    public AnsibleLifecycle(TransformationContext context) {
        super(context);
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Ansible...");
        AnsibleVisitor visitor = new AnsibleVisitor(context);
        VisitorHelper.visit(context.getModel().getComponents(), visitor, component -> component instanceof Compute);
        EdgeReversedGraph<RootComponent, RootRelation> dependencyGraph
            = new EdgeReversedGraph<>(context.getModel().getTopology());
        TopologicalOrderIterator<RootComponent, RootRelation> iterator
            = new TopologicalOrderIterator<>(dependencyGraph);
        while (iterator.hasNext()) {
            iterator.next().accept(visitor);
        }
        visitor.populate();
        logger.info("Transformation to Ansible successful");
    }
}
