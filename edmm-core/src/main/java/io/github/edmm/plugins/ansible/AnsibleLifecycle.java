package io.github.edmm.plugins.ansible;

import freemarker.template.Configuration;
import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.plugin.AbstractLifecycle;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.VisitorHelper;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class AnsibleLifecycle extends AbstractLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnsibleLifecycle.class);
    public static final String FILE_NAME = "deployment.yml";

    private final TransformationContext context;

    public AnsibleLifecycle(TransformationContext context) {
        this.context = context;
    }

    @Override
    public void prepare() {
        LOGGER.info("Prepare transformation for Ansible...");
    }

    @Override
    public void transform() {
        LOGGER.info("Begin transformation to Ansible...");
        AnsibleVisitor visitor = new AnsibleVisitor(context);
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        visitor.populateAnsibleFile();
        LOGGER.info("Transformation to Ansible successful");
    }

    @Override
    public void cleanup() {
        LOGGER.info("Cleanup transformation leftovers...");
    }




}
