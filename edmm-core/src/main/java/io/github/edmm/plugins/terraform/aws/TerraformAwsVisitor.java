package io.github.edmm.plugins.terraform.aws;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.github.edmm.core.BashScript;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.TemplateHelper;
import io.github.edmm.core.TopologyGraphHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.Artifact;
import io.github.edmm.model.Operation;
import io.github.edmm.model.component.Auth0;
import io.github.edmm.model.component.AwsAurora;
import io.github.edmm.model.component.AwsBeanstalk;
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
import io.github.edmm.plugins.terraform.model.Auth0ResourceServer;
import io.github.edmm.plugins.terraform.model.Aws;
import io.github.edmm.plugins.terraform.model.FileProvisioner;
import io.github.edmm.plugins.terraform.model.RemoteExecProvisioner;
import io.github.edmm.utils.Consts;

import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.model.component.WebServer.PORT;

public class TerraformAwsVisitor extends TerraformVisitor {

    private static final Logger logger = LoggerFactory.getLogger(TerraformAwsVisitor.class);

    private final Map<Compute, Aws.Instance> computeInstances = new HashMap<>();
    private final Map<RootComponent, Aws.DbInstance> dbInstances = new HashMap<>();
    private final Map<RootComponent, Aws.Beanstalk> beanstalkComponents = new HashMap<>();
    private final Map<RootComponent, Auth0ResourceServer> auth0Instances = new HashMap<>();

    public TerraformAwsVisitor(TransformationContext context) {
        super(context);
    }

    @Override
    public void populateTerraformFile() {
        PluginFileAccess fileAccess = context.getFileAccess();
        Map<String, Object> data = new HashMap<>();
        data.put("instances", computeInstances);
        data.put("dbInstances", dbInstances);
        data.put("beanstalkComponents", beanstalkComponents);
        data.put("auth0Instances", auth0Instances);
        try {
            fileAccess.append("aws.tf", TemplateHelper.toString(cfg, "aws.tf", data));
            if (auth0Instances.size() > 0) {
                fileAccess.append("auth0.tf", TemplateHelper.toString(cfg, "auth0.tf", data));
            }
        } catch (IOException e) {
            logger.error("Failed to write Terraform file", e);
            throw new TransformationException(e);
        }
        for (Aws.Instance awsInstance : computeInstances.values()) {
            // Copy artifacts to target directory
            for (FileProvisioner provisioner : awsInstance.getFileProvisioners()) {
                try {
                    fileAccess.copy(provisioner.getSource(), provisioner.getSource());
                } catch (IOException e) {
                    logger.warn("Failed to copy file '{}'", provisioner.getSource());
                }
            }
            // Copy operations to target directory
            List<String> operations = awsInstance.getRemoteExecProvisioners().stream()
                .map(RemoteExecProvisioner::getScripts)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
            for (String op : operations) {
                try {
                    fileAccess.copy(op, op);
                } catch (IOException e) {
                    logger.warn("Failed to copy file '{}'", op);
                }
            }
            // Write env.sh script entries
            BashScript envScript = new BashScript(fileAccess, "env.sh");
            awsInstance.getEnvVars().forEach((name, value) -> envScript.append("export " + name + "=" + value));
        }
    }

    @Override
    public void visit(Auth0 component) {
        Auth0ResourceServer auth0 = new Auth0ResourceServer();
        auth0.setName(component.getNormalizedName());
        auth0.setDomain(component.getDomain());
        auth0.setScopes(Arrays.stream(component.getScopes().split(",")).collect(Collectors.toList()));
        auth0.setIdentifier(component.getIdentifier());
        component.getClientId().ifPresent(auth0::setClientId);
        component.getClientSecret().ifPresent(auth0::setClientSecret);
        auth0Instances.put(component, auth0);
        component.setTransformed(true);
    }

    @Override
    public void visit(AwsAurora component) {
        Aws.DbInstance dbInstance = new Aws.DbInstance();
        dbInstances.put(component, dbInstance);
        component.setTransformed(true);
    }

    @Override
    public void visit(AwsBeanstalk component) {
        Aws.Beanstalk beanstalk = new Aws.Beanstalk();
        beanstalkComponents.put(component, beanstalk);
        component.setTransformed(true);
    }

    @Override
    public void visit(Compute component) {
        Aws.Instance awsInstance = Aws.Instance.builder()
            .name(component.getNormalizedName())
            // TODO: Try to resolve image
            .ami("ami-0bbc25e23a7640b9b")
            // TODO: Try to resolve instance type
            .instanceType("t2.micro")
            .build();
        List<String> operations = collectOperations(component);
        awsInstance.addRemoteExecProvisioner(new RemoteExecProvisioner(operations));
        awsInstance.addFileProvisioner(new FileProvisioner("./env.sh", "/opt/env.sh"));
        computeInstances.put(component, awsInstance);
        component.setTransformed(true);
    }

    @Override
    public void visit(ConnectsTo relation) {
        RootComponent source = graph.getEdgeSource(relation);
        RootComponent target = graph.getEdgeTarget(relation);
        Optional<RootComponent> optionalSource = TopologyGraphHelper.resolveHostingComponent(graph, source);
        Optional<RootComponent> optionalTarget = TopologyGraphHelper.resolveHostingComponent(graph, target);
        if (optionalSource.isPresent() && optionalTarget.isPresent()) {
            RootComponent sourceComponent = optionalSource.get();
            RootComponent targetComponent = optionalTarget.get();
            if (sourceComponent instanceof Compute && targetComponent instanceof Compute) {
                Aws.Instance sourceCompute = computeInstances.get(sourceComponent);
                Aws.Instance targetCompute = computeInstances.get(targetComponent);
                sourceCompute.addDependency("aws_instance." + targetCompute.getName());
            }
        }
    }

    @Override
    public void visit(RootComponent component) {
        collectIngressPorts(component);
        collectFileProvisioners(component);
        collectRemoteExecProvisioners(component);
        collectEnvVars(component);
        component.setTransformed(true);
    }

    private void collectIngressPorts(RootComponent component) {
        component.getProperty(PORT).ifPresent(port -> {
            Optional<Compute> optionalCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component);
            if (optionalCompute.isPresent()) {
                Compute hostingCompute = optionalCompute.get();
                computeInstances.get(hostingCompute).addIngressPort(String.valueOf(port));
            }
        });
    }

    private void collectFileProvisioners(RootComponent component) {
        Optional<Compute> optionalCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component);
        if (optionalCompute.isPresent()) {
            Compute hostingCompute = optionalCompute.get();
            Aws.Instance awsInstance = computeInstances.get(hostingCompute);
            for (Artifact artifact : component.getArtifacts()) {
                String destination = "/opt/" + component.getNormalizedName();
                awsInstance.addFileProvisioner(new FileProvisioner(artifact.getValue(), destination));
            }
        }
    }

    private void collectRemoteExecProvisioners(RootComponent component) {
        Optional<Compute> optionalCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component);
        if (optionalCompute.isPresent()) {
            Compute hostingCompute = optionalCompute.get();
            Aws.Instance awsInstance = computeInstances.get(hostingCompute);
            List<String> operations = collectOperations(component);
            awsInstance.addRemoteExecProvisioner(new RemoteExecProvisioner(operations));
        }
    }

    private void collectEnvVars(RootComponent component) {
        Optional<Compute> optionalCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component);
        if (optionalCompute.isPresent()) {
            Compute hostingCompute = optionalCompute.get();
            Aws.Instance awsInstance = computeInstances.get(hostingCompute);
            String[] blacklist = {"key_name", "public_key"};
            component.getProperties().values().stream()
                .filter(p -> !Arrays.asList(blacklist).contains(p.getName()))
                .forEach(p -> {
                    String name = (component.getNormalizedName() + "_" + p.getNormalizedName()).toUpperCase();
                    awsInstance.addEnvVar(name, p.getValue());
                });
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
        Optional<RootComponent> optionalHostingComponent = TopologyGraphHelper.resolveHostingComponent(graph, component);
        if (optionalHostingComponent.isPresent()) {
            RootComponent hostingComponent = optionalHostingComponent.get();
            if (hostingComponent instanceof AwsAurora) {
                Aws.DbInstance db = dbInstances.get(hostingComponent);
                db.setName(component.getNormalizedName());
                component.getUser().ifPresent(db::setUsername);
                component.getPassword().ifPresent(db::setPassword);
                // TODO: Upload SQL file?
                component.getArtifacts().stream().findFirst().ifPresent(artifact -> {
                    // File file = new File(artifact.getValue());
                    // String filepath = FilenameUtils.normalize(file.getParentFile().getAbsolutePath());
                    // String filename = file.getName();
                });
                component.setTransformed(true);
                return;
            }
        }
        visit((RootComponent) component);
    }

    @Override
    @SneakyThrows
    public void visit(WebApplication component) {
        Optional<RootComponent> optionalHostingComponent = TopologyGraphHelper.resolveHostingComponent(graph, component);
        if (optionalHostingComponent.isPresent()) {
            RootComponent hostingComponent = optionalHostingComponent.get();
            if (hostingComponent instanceof AwsBeanstalk) {
                Aws.Beanstalk beanstalk = beanstalkComponents.get(hostingComponent);
                beanstalk.setName(component.getNormalizedName());
                component.getArtifacts().stream().findFirst().ifPresent(artifact -> {
                    File file = new File(artifact.getValue());
                    String filepath = FilenameUtils.normalize(file.getParentFile().getAbsolutePath());
                    beanstalk.setFilepath(filepath + Consts.FS);
                    beanstalk.setFilename(file.getName());
                });
                component.setTransformed(true);
                return;
            }
        }
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
