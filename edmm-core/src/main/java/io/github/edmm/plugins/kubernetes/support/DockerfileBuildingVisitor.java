package io.github.edmm.plugins.kubernetes.support;

import java.util.Map;

import io.github.edmm.core.TransformationHelper;
import io.github.edmm.core.parser.support.DefaultKeys;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.docker.Container;
import io.github.edmm.docker.DockerfileBuilder;
import io.github.edmm.docker.FileMapping;
import io.github.edmm.docker.PortMapping;
import io.github.edmm.model.Artifact;
import io.github.edmm.model.Operation;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.MongoDb;
import io.github.edmm.model.component.MongoDbSchema;
import io.github.edmm.model.component.MysqlDatabase;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.RabbitMq;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.visitor.ComponentVisitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.model.component.Compute.PUBLIC_ADDRESS;
import static io.github.edmm.model.component.WebServer.PORT;

public class DockerfileBuildingVisitor implements ComponentVisitor {

    private static final Logger logger = LoggerFactory.getLogger(DockerfileBuildingVisitor.class);

    private final Container stack;
    private final PluginFileAccess fileAccess;
    private final DockerfileBuilder builder;

    public DockerfileBuildingVisitor(Container stack, PluginFileAccess fileAccess) {
        this.stack = stack;
        this.fileAccess = fileAccess;
        this.builder = new DockerfileBuilder().compress();
        this.builder.from(stack.getBaseImage());
        this.builder.workdir("/opt/edmm");
    }

    public void populateDockerfile() {
        String targetDirectory = stack.getLabel();
        stack.getComponents().forEach(component -> component.accept(this));
        try {
            for (FileMapping mapping : stack.getArtifacts()) {
                String sourcePath = mapping.getArtifact().getValue();
                String filename = determineFilename(mapping.getArtifact());
                String targetPath = targetDirectory + "/" + filename;
                fileAccess.copy(sourcePath, targetPath);
                builder.add("./" + filename, filename);
            }
            for (FileMapping mapping : stack.getOperations()) {
                if (builder.isSkipCreateOperation()
                    && mapping.getOperation().getName().equalsIgnoreCase(DefaultKeys.CREATE)) {
                    continue;
                }
                String sourcePath = mapping.getArtifact().getValue();
                String filename = mapping.getComponent().getNormalizedName() +
                    "_" + determineFilename(mapping.getArtifact());
                String targetPath = targetDirectory + "/" + filename;
                fileAccess.copy(sourcePath, targetPath);
                builder.add("./" + filename, filename);
                builder.run("./" + filename);
            }
            // Expose ports
            stack.getPorts().forEach(port -> builder.expose(port.getPort()));
            // Add final CMD statement
            if (!builder.isSkipStartOperation() && !stack.getStartOperations().isEmpty()) {
                FileMapping mapping = stack.getStartOperations().get(stack.getStartOperations().size() - 1);
                String sourcePath = mapping.getArtifact().getValue();
                String filename = mapping.getComponent().getNormalizedName() +
                    "_" + determineFilename(mapping.getArtifact());
                String targetPath = targetDirectory + "/" + filename;
                fileAccess.copy(sourcePath, targetPath);
                builder.add("./" + filename, filename);
                builder.cmd("./" + filename);
            }
            fileAccess.write(targetDirectory + "/Dockerfile", builder.build());
        } catch (Exception e) {
            logger.error("Failed to create Dockerfile for stack '{}'", stack.getName(), e);
            throw new TransformationException(e);
        }
    }

    private String determineFilename(Artifact artifact) {
        String name = artifact.getValue();
        if (name.contains("/")) {
            return name.substring(name.lastIndexOf("/") + 1);
        } else {
            return name;
        }
    }

    private void collectPorts(RootComponent component) {
        component.getProperty(PORT)
            .ifPresent(port -> stack.addPort(new PortMapping(component.getNormalizedName(), port)));
    }

    private void collectEnvVars(RootComponent component) {
        Map<String, String> envVars = TransformationHelper.collectEnvVars(component);
        envVars.forEach(builder::env);
    }

    private void collectLifecycleOperation(RootComponent component, Operation operation, boolean isStartOperation) {
        for (Artifact artifact : operation.getArtifacts()) {
            FileMapping mapping = new FileMapping(component, operation, artifact);
            if (!isStartOperation) {
                stack.addOperation(mapping);
            } else {
                stack.addStartOperation(mapping);
            }
        }
    }

    private void collectArtifacts(RootComponent component) {
        for (Artifact artifact : component.getArtifacts()) {
            FileMapping mapping = new FileMapping(component, null, artifact);
            stack.addArtifact(mapping);
        }
    }

    @Override
    public void visit(RootComponent component) {
        collectPorts(component);
        collectEnvVars(component);
        RootComponent.StandardLifecycle lifecycle = component.getStandardLifecycle();
        lifecycle.getCreate().ifPresent(op -> collectLifecycleOperation(component, op, false));
        lifecycle.getConfigure().ifPresent(op -> collectLifecycleOperation(component, op, false));
        lifecycle.getStart().ifPresent(op -> collectLifecycleOperation(component, op, true));
        collectArtifacts(component);
    }

    @Override
    public void visit(Compute component) {
        visit((RootComponent) component);
        component.addProperty(PUBLIC_ADDRESS.getName(), stack.getPublicAddress());
    }

    @Override
    public void visit(MysqlDatabase component) {
        visit((RootComponent) component);
        builder.env("MYSQL_DATABASE", component.getSchemaName());
        component.getUser().ifPresent(value -> builder.env("MYSQL_USER", value));
        component.getPassword().ifPresent(value -> builder.env("MYSQL_PASSWORD", value));
        builder.env("MYSQL_ALLOW_EMPTY_PASSWORD", "yes");
    }

    @Override
    public void visit(MysqlDbms component) {
        visit((RootComponent) component);
        component.getRootPassword().ifPresent(value -> builder.env("MYSQL_ROOT_PASSWORD", value));
        builder.setSkipStartOperation(true);
        builder.setSkipCreateOperation(true);
    }

    @Override
    public void visit(RabbitMq component) {
        component.getProperty(PORT)
            .ifPresent(port -> stack.addPort(new PortMapping(component.getNormalizedName(), port, port, "TCP")));
        stack.addPort(new PortMapping("management", component.getManagementPort()));
        visit((RootComponent) component);
        builder.env("RABBITMQ_DEFAULT_USER", "guest");
        builder.env("RABBITMQ_DEFAULT_PASS", "guest");
        builder.setSkipStartOperation(true);
        builder.setSkipCreateOperation(true);
    }

    @Override
    public void visit(MongoDb component) {
        component.getProperty(PORT)
            .ifPresent(port -> stack.addPort(new PortMapping(component.getNormalizedName(), 27017, port, null)));
        visit((RootComponent) component);
        component.getRootUser().ifPresent(value -> builder.env("MONGO_INITDB_ROOT_USERNAME", value));
        component.getRootPassword().ifPresent(value -> builder.env("MONGO_INITDB_ROOT_PASSWORD", value));
        builder.setSkipStartOperation(true);
        builder.setSkipCreateOperation(true);
    }

    @Override
    public void visit(MongoDbSchema component) {
        visit((RootComponent) component);
        builder.env("MONGO_INITDB_DATABASE", component.getSchemaName());
    }
}
