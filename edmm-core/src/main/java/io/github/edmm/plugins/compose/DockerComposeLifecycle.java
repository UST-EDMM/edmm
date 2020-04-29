package io.github.edmm.plugins.compose;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.support.CheckModelResult;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.docker.Container;
import io.github.edmm.model.visitor.VisitorHelper;
import io.github.edmm.plugins.ComputeSupportVisitor;
import io.github.edmm.plugins.compose.support.DockerComposeBuilder;
import io.github.edmm.plugins.kubernetes.KubernetesLifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerComposeLifecycle extends KubernetesLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(DockerComposeLifecycle.class);

    public DockerComposeLifecycle(TransformationContext context) {
        super(context);
    }

    @Override
    public CheckModelResult checkModel() {
        ComputeSupportVisitor visitor = new ComputeSupportVisitor(context);
        VisitorHelper.visit(context.getModel().getComponents(), visitor);
        return visitor.getResult();
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to Docker Compose...");
        PluginFileAccess fileAccess = context.getFileAccess();
        for (Container stack : containers) {
            resolveBaseImage(stack);
            buildDockerfile(stack, fileAccess);
        }
        DockerComposeBuilder composeBuilder = new DockerComposeBuilder(containers, dependencyGraph, fileAccess);
        composeBuilder.populateComposeFile();
        logger.info("Transformation to Docker Compose successful");
    }
}
