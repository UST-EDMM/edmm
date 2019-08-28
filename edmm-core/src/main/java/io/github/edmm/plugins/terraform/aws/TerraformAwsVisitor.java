package io.github.edmm.plugins.terraform.aws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import io.github.edmm.core.plugin.GraphHelper;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.model.Operation;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Database;
import io.github.edmm.model.component.Dbms;
import io.github.edmm.model.component.MysqlDatabase;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.Tomcat;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.plugins.terraform.TerraformVisitor;
import io.github.edmm.plugins.terraform.model.Provisioner;
import io.github.edmm.plugins.terraform.model.aws.Ec2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.model.component.WebServer.PORT;
import static io.github.edmm.plugins.terraform.TerraformLifecycle.FILE_NAME;

public class TerraformAwsVisitor extends TerraformVisitor {

    private static final Logger logger = LoggerFactory.getLogger(TerraformAwsVisitor.class);

    private final Map<Compute, Ec2> computeInstances = new HashMap<>();

    public TerraformAwsVisitor(TransformationContext context) {
        super(context);
    }

    @Override
    public void populateTerraformFile() {
        PluginFileAccess fileAccess = context.getFileAccess();
        Map<String, Object> data = new HashMap<>();
        // Populate the data
        data.put("instances", computeInstances);
        try {
            fileAccess.append(FILE_NAME, TemplateHelper.toString(cfg, "aws.tf", data));
        } catch (IOException e) {
            logger.error("Failed to write Terraform file: {}", e.getMessage(), e);
        }
    }

    @Override
    public void visit(Compute component) {
        Ec2 ec2 = Ec2.builder()
                .name(component.getNormalizedName())
                // TODO: Try to resolve image
                .ami("ami-0bbc25e23a7640b9b")
                // TODO: Try to resolve instance type
                .instanceType("t2.micro")
                .ingressPorts(Lists.newArrayList())
                .provisioners(Lists.newArrayList())
                .dependsOn(Lists.newArrayList())
                .build();
        List<String> operations = collectOperations(component);
        ec2.getProvisioners().add(Provisioner.builder().operations(operations).build());
        computeInstances.put(component, ec2);
        component.setTransformed(true);
    }

    @Override
    public void visit(ConnectsTo relation) {
        RootComponent source = graph.getEdgeSource(relation);
        RootComponent target = graph.getEdgeTarget(relation);
        Optional<Compute> optionalSourceCompute = GraphHelper.resolveHostingComputeComponent(graph, source);
        Optional<Compute> optionalTargetCompute = GraphHelper.resolveHostingComputeComponent(graph, target);
        if (optionalSourceCompute.isPresent() && optionalTargetCompute.isPresent()) {
            Ec2 sourceCompute = computeInstances.get(optionalSourceCompute.get());
            Ec2 targetCompute = computeInstances.get(optionalTargetCompute.get());
            sourceCompute.getDependsOn().add(targetCompute.getName());
        }
    }

    @Override
    public void visit(RootComponent component) {
        collectIngressPorts(component);
        collectProvisioners(component);
        component.setTransformed(true);
    }

    private void collectIngressPorts(RootComponent component) {
        component.getProperty(PORT).ifPresent(port -> {
            Optional<Compute> optionalCompute = GraphHelper.resolveHostingComputeComponent(graph, component);
            if (optionalCompute.isPresent()) {
                Compute hostingCompute = optionalCompute.get();
                computeInstances.get(hostingCompute).getIngressPorts().add(String.valueOf(port));
            }
        });
    }

    private void collectProvisioners(RootComponent component) {
        Optional<Compute> optionalCompute = GraphHelper.resolveHostingComputeComponent(graph, component);
        if (optionalCompute.isPresent()) {
            Compute hostingCompute = optionalCompute.get();
            Ec2 ec2 = computeInstances.get(hostingCompute);
            List<String> operations = collectOperations(component);
            ec2.getProvisioners().add(Provisioner.builder().operations(operations).build());
        }
    }

    @Override
    public void visit(Tomcat component) {
        visit((RootComponent) component);
    }

    @Override
    public void visit(MysqlDbms component) {
        visit((RootComponent) component);
    }

    @Override
    public void visit(Database component) {
        visit((RootComponent) component);
    }

    @Override
    public void visit(Dbms component) {
        visit((RootComponent) component);
    }

    @Override
    public void visit(MysqlDatabase component) {
        visit((RootComponent) component);
    }

    @Override
    public void visit(WebApplication component) {
        visit((RootComponent) component);
    }

    private List<String> collectOperations(RootComponent component) {
        List<String> operations = new ArrayList<>();
        Consumer<Operation> artifactsConsumer = op -> op.getArtifacts()
                .forEach(artifact -> operations.add(artifact.getValue()));
        component.getStandardLifecycle().getCreate().ifPresent(artifactsConsumer);
        component.getStandardLifecycle().getConfigure().ifPresent(artifactsConsumer);
        component.getStandardLifecycle().getStart().ifPresent(artifactsConsumer);
        return operations;
    }
}
