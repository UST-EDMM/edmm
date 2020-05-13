package io.github.edmm.plugins.azure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.edmm.core.TopologyGraphHelper;
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
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.visitor.ComponentVisitor;
import io.github.edmm.model.visitor.RelationVisitor;
import io.github.edmm.plugins.azure.model.ResourceManagerTemplate;
import io.github.edmm.plugins.azure.model.resource.ResourceTypeEnum;
import io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.VirtualMachine;
import io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.VirtualMachineProperties;
import io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions.EnvVarVirtualMachineExtension;
import io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions.VirtualMachineExtension;
import io.github.edmm.plugins.azure.model.resource.network.networkinterfaces.NetworkInterface;
import io.github.edmm.plugins.azure.model.resource.network.virtualnetworks.VirtualNetwork;
import io.github.edmm.plugins.azure.model.resource.storage.storageaccounts.StorageAccount;

import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.model.component.WebServer.PORT;

public class AzureVisitor implements ComponentVisitor, RelationVisitor {

    private static final Logger logger = LoggerFactory.getLogger(AzureVisitor.class);

    private final Graph<RootComponent, RootRelation> graph;

    @Getter
    private final ResourceManagerTemplate resultTemplate;

    public AzureVisitor(Graph<RootComponent, RootRelation> graph) {
        this.graph = graph;
        this.resultTemplate = new ResourceManagerTemplate();
    }

    @Override
    public void visit(Compute component) {
        long vmCount = resultTemplate.getResources().stream().filter(resource -> resource.getType().equals(ResourceTypeEnum.VIRTUAL_MACHINES)).count();
        logger.debug("Processing compute node #{}", vmCount + 1);

        // This is the first VM we add, therefore we apply defaults
        if (vmCount == 0) {
            final VirtualNetwork virtualNetwork = new VirtualNetwork();
            final StorageAccount storageProfile = new StorageAccount();
            resultTemplate.getResources().add(virtualNetwork);
            resultTemplate.getResources().add(storageProfile);
        }

        final String name = "vm_" + component.getNormalizedName();
        final VirtualMachine vm = new VirtualMachine(name);
        resultTemplate.getResources().add(vm);
        Optional<String> sshPublicKey = component.getPublicKey();

        // Check SSH public key
        if (sshPublicKey.isPresent()) {
            vm.setAuthentication(false, sshPublicKey.get());
            logger.info("Setting an ssh public key for vm " + name);
        }

        // Check OS and image
        Optional<String> osFamily = component.getOsFamily();
        if (osFamily.isPresent() && !osFamily.get().toLowerCase().contains("linux")) {
            throw new TransformationException("Only linux-based operating systems are currently supported!");
        }
        Optional<String> image = component.getMachineImage();
        if (image.isPresent() && !image.get().toLowerCase().contains("ubuntu")) {
            throw new TransformationException("Only Ubuntu OS is currently supported!");
        }

        // Check self properties (turn them into environment variables)
        this.collectEnvironmentVariables(component);
        // Check self operations
        this.collectVmExtensions(component);

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
            sourceCompute.getDependsOn().add(targetCompute.getFullName());

            // add target env vars to source env vars (e.g., database-related properties like username)
            EnvVarVirtualMachineExtension sourceEnvVarExt = this.getEnvVarVmExtension(source);
            EnvVarVirtualMachineExtension targetEnvVarExt = this.getEnvVarVmExtension(target);
            sourceEnvVarExt.getEnvironmentVariables().putAll(targetEnvVarExt.getEnvironmentVariables());
            // add the target host name to the source environment variables.
            this.getInternalHostname(targetCompute).ifPresent(hostname -> sourceEnvVarExt.getEnvironmentVariables().put(
                String.format("%s_HOSTNAME", target.getNormalizedName()).toUpperCase(),
                hostname
            ));
        }
    }

    @Override
    public void visit(RootComponent component) {
        collectIngressPorts(component);
        collectEnvironmentVariables(component);
        collectVmExtensions(component);
        component.setTransformed(true);
    }

    private void collectIngressPorts(RootComponent component) {
        component.getProperty(PORT).ifPresent(port -> {
            Optional<VirtualMachine> vm = this.getHostingVirtualMachine(component);
            vm.ifPresent(virtualMachine -> virtualMachine.addPort(component.getNormalizedName(), String.valueOf(port)));
        });
    }

    private void collectEnvironmentVariables(RootComponent component) {
        EnvVarVirtualMachineExtension extension = this.getEnvVarVmExtension(component);
        extension.addEnvironmentVariables(component);
    }

    private void collectVmExtensions(RootComponent component) {
        Optional<VirtualMachine> vm = this.getHostingVirtualMachine(component);

        if (vm.isPresent()) {
            List<Pair<String, String>> artifacts = collectOperations(component);
            List<VirtualMachineExtension> existingExtensions = this.getExistingExtensions(vm.get());
            VirtualMachineExtension currentExtension;

            for (Pair<String, String> artifact : artifacts) {
                // Create new extension (script) and set its name
                currentExtension = new VirtualMachineExtension(vm.get(), component.getNormalizedName(), artifact.getKey());
                // Set the path of the script file to be executed
                currentExtension.setScriptPath(artifact.getValue());
                // Get dependencies of the vm extension
                List<String> dependencies = currentExtension.getDependsOn();
                // Set dependencies on previous scripts
                existingExtensions.forEach(extension -> dependencies.add(extension.getFullName()));
                // Add extension to resources
                this.resultTemplate.getResources().add(currentExtension);
                existingExtensions.add(currentExtension);
            }
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

    private List<Pair<String, String>> collectOperations(RootComponent component) {
        List<Pair<String, String>> allArtifacts = new ArrayList<>();

        component.getStandardLifecycle().getCreate().ifPresent(operation -> this.addArtifact(allArtifacts, "create", operation));
        component.getStandardLifecycle().getConfigure().ifPresent(operation -> this.addArtifact(allArtifacts, "configure", operation));
        component.getStandardLifecycle().getStart().ifPresent(operation -> this.addArtifact(allArtifacts, "start", operation));

        return allArtifacts;
    }

    private void addArtifact(List<Pair<String, String>> existingArtifacts, String operationBaseName, Operation operation) {
        // only consider the first artifact in an operation
        operation.getArtifacts().stream().findFirst().ifPresent(artifact ->
            existingArtifacts.add(ImmutablePair.of(operationBaseName, artifact.getValue())));
    }

    private Optional<VirtualMachine> getHostingVirtualMachine(RootComponent component) {
        Compute hostingCompute = null;

        // First check if the component is a Compute node
        if (component instanceof Compute) {
            hostingCompute = (Compute) component;
        } else {
            // Now check if it is hosted by a compute node
            Optional<Compute> optionalCompute = TopologyGraphHelper.resolveHostingComputeComponent(graph, component);
            if (optionalCompute.isPresent()) {
                hostingCompute = optionalCompute.get();
            }
        }

        if (hostingCompute != null) {
            final String vmName = "vm_" + hostingCompute.getNormalizedName();
            return this.resultTemplate.getResources()
                .stream()
                .filter(resource -> resource.getName().equals(vmName) && resource instanceof VirtualMachine)
                .map(resource -> (VirtualMachine) resource)
                .findFirst();
        } else {
            throw new IllegalArgumentException(String.format("This component is not a Compute node nor is it hosted by a Compute node: %s", component.getNormalizedName()));
        }
    }

    private List<VirtualMachineExtension> getExistingExtensions(VirtualMachine virtualMachine) {
        return this.resultTemplate
            .getResources()
            .stream()
            .filter(resource -> resource instanceof VirtualMachineExtension &&
                resource.getDependsOn().contains(virtualMachine.getFullName())
            )
            .map(resource -> (VirtualMachineExtension) resource)
            .collect(Collectors.toList());
    }

    private Optional<String> getInternalHostname(VirtualMachine vm) {
        Optional<NetworkInterface> nicOpt = ((VirtualMachineProperties) vm.getProperties()).getNetworkProfile().getNetworkInterfaces().stream().findFirst();
        Optional<String> result;
        result = nicOpt.map(NetworkInterface::getInternalDnsNameLabel);

        return result;
    }

    /**
     * This also creates and adds the extension if it is not already created!
     */
    private EnvVarVirtualMachineExtension getEnvVarVmExtension(RootComponent component) {
        Optional<VirtualMachine> vmOpt = getHostingVirtualMachine(component);
        EnvVarVirtualMachineExtension result = null;

        if (vmOpt.isPresent()) {
            VirtualMachine vm = vmOpt.get();
            List<VirtualMachineExtension> extensions = this.getExistingExtensions(vm);

            if (extensions.size() == 0) {
                result = new EnvVarVirtualMachineExtension(vm);
                resultTemplate.getResources().add(result);
            } else {
                Optional<VirtualMachineExtension> envVarExtOpt = extensions.stream().filter(ext -> ext instanceof EnvVarVirtualMachineExtension).findFirst();

                if (!envVarExtOpt.isPresent()) {
                    throw new IllegalStateException("The virtual machine extension resource that describes environment variables should be the first extension added!");
                } else {
                    result = (EnvVarVirtualMachineExtension) envVarExtOpt.get();
                }
            }
        }

        return result;
    }
}
