package io.github.edmm.plugins.cloudify.azure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.plugin.TemplateHelper;
import io.github.edmm.core.plugin.TopologyGraphHelper;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationException;
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
import io.github.edmm.plugins.cloudify.CloudifyVisitor;
import io.github.edmm.plugins.cloudify.model.azure.Script;
import io.github.edmm.plugins.cloudify.model.azure.VirtualMachine;
import io.github.edmm.plugins.terraform.aws.TerraformAwsVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.model.component.WebServer.PORT;
import static io.github.edmm.plugins.cloudify.CloudifyLifecycle.FILE_NAME;

public class CloudifyAzureVisitor extends CloudifyVisitor {

    private static final Logger logger = LoggerFactory.getLogger(TerraformAwsVisitor.class);

    private final Map<Compute, VirtualMachine> computeInstances = new HashMap<>();

    public CloudifyAzureVisitor(TransformationContext context) {
        super(context);
    }

    @Override
    public void populateCloudifyFile() {
        PluginFileAccess fileAccess = context.getFileAccess();
        Map<String, Object> data = new HashMap<>();
        data.put("computes", computeInstances);
        try {
            fileAccess.append(FILE_NAME, TemplateHelper.toString(cfg, "azure.yml", data));
        } catch (IOException e) {
            logger.error("Failed to write Terraform file", e);
            throw new TransformationException(e);
        }
        for (VirtualMachine vm : computeInstances.values()) {
            // Copy artifacts to target directory
            for (io.github.edmm.plugins.cloudify.model.azure.Operation operation : vm.getOperations()) {
                for (Script script : operation.getScripts()) {
                    try {
                        fileAccess.copy(script.getPath(), script.getPath());
                    } catch (IOException e) {
                        logger.warn("Failed to copy file '{}'", script.getPath());
                    }
                }
            }
        }
    }

    @Override
    public void visit(Compute component) {
        VirtualMachine vm = VirtualMachine.builder()
                .name(component.getNormalizedName())
                .ingressPorts(new ArrayList<>())
                .dependsOn(new ArrayList<>())
                .operations(new ArrayList<>())
                .build();
        this.computeInstances.put(component, vm);
        Optional<String> sshPublicKey = component.getPublicKey();

        // Check ssh public key
        if (sshPublicKey.isPresent()) {
            vm.setPasswordAuthentication(false);
            vm.setPassword(null);
            vm.setSsh(sshPublicKey.get());
            logger.info("Setting an ssh public key for vm " + vm.getName());
        }

        // Check the OS
        Optional<String> osFamily = component.getOsFamily();
        Optional<String> image = component.getMachineImage();

        if ((osFamily.isPresent() && !osFamily.get().toLowerCase().contains("linux")) || (image.isPresent() && !image.get().toLowerCase().contains("ubuntu"))) {
            throw new TransformationException("Only Ubuntu Linux OS is currently supported!");
        }

        // Add self operations
        this.collectOperations(component);

        component.setTransformed(true);
    }

    @Override
    public void visit(ConnectsTo relation) {
        RootComponent source = graph.getEdgeSource(relation);
        RootComponent target = graph.getEdgeTarget(relation);
        Optional<VirtualMachine> optionalSourceVm = this.getHostingVirtualMachine(source);
        Optional<VirtualMachine> optionalTargetVm = this.getHostingVirtualMachine(target);

        if (optionalSourceVm.isPresent() && optionalTargetVm.isPresent()) {
            VirtualMachine sourceCompute = optionalSourceVm.get();
            VirtualMachine targetCompute = optionalTargetVm.get();
            sourceCompute.addDependency(targetCompute);
        }
    }

    @Override
    public void visit(RootComponent component) {
        collectIngressPorts(component);
        collectOperations(component);
        component.setTransformed(true);
    }

    private void collectIngressPorts(RootComponent component) {
        component.getProperty(PORT).ifPresent(port -> {
            Optional<VirtualMachine> vm = this.getHostingVirtualMachine(component);
            vm.ifPresent(virtualMachine -> virtualMachine.addIngressPort(component.getNormalizedName(), String.valueOf(port)));
        });
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

    private void collectOperations(RootComponent component) {
        Optional<VirtualMachine> vmOpt = this.getHostingVirtualMachine(component);

        if (vmOpt.isPresent()) {
            VirtualMachine vm = vmOpt.get();
            component.getStandardLifecycle().getCreate().ifPresent(operation -> this.addArtifacts(component, vm, operation));
            component.getStandardLifecycle().getConfigure().ifPresent(operation -> this.addArtifacts(component, vm, operation));
            component.getStandardLifecycle().getStart().ifPresent(operation -> this.addArtifacts(component, vm, operation));
        }
    }

    private void addArtifacts(RootComponent component, VirtualMachine vm, Operation operation) {
        operation.getArtifacts().forEach(artifact -> {
                    String componentName = component.getNormalizedName();
                    String operationName = operation.getNormalizedName();
                    String artifactName = artifact.getName();
                    String artifactPath = artifact.getValue();
                    vm.addScript(componentName, operationName, artifactName, artifactPath);
                }
        );
    }

    private Optional<VirtualMachine> getHostingVirtualMachine(RootComponent component) {
        Compute hostingCompute = null;

        // first check if the component is a Compute node
        if (component instanceof Compute) {
            hostingCompute = (Compute) component;
        } else {
            // now check if it is hosted by a compute node
            Optional<Compute> optionalCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component);
            if (optionalCompute.isPresent()) {
                hostingCompute = optionalCompute.get();
            }
        }

        if (hostingCompute != null) {
            if (this.computeInstances.containsKey(hostingCompute)) {
                return Optional.of(this.computeInstances.get(hostingCompute));
            } else {
                // this should not happen!
                return Optional.empty();
            }
        } else {
            throw new IllegalArgumentException(String.format("This component is not a Compute node nor is it hosted by a Compute node: %s", component.getNormalizedName()));
        }
    }
}
