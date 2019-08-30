package io.github.edmm.plugins.azure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.edmm.core.plugin.TopologyGraphHelper;
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
import io.github.edmm.plugins.azure.model.resource.compute.virtualmachines.extensions.VirtualMachineExtension;
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
        long previousVmCount = resultTemplate.getResources().stream().filter(resource -> resource.getType().equals(ResourceTypeEnum.VIRTUAL_MACHINES)).count();
        logger.debug("Processing compute node #{}", previousVmCount + 1);

        // this is the first vm we add!
        if (previousVmCount == 0) {
            final VirtualNetwork virtualNetwork = new VirtualNetwork();
            final StorageAccount storageProfile = new StorageAccount();
            resultTemplate.getResources().add(virtualNetwork);
            resultTemplate.getResources().add(storageProfile);
        }
        final String vmName = "vm_" + component.getNormalizedName();
        final VirtualMachine vm = new VirtualMachine(vmName);
        resultTemplate.getResources().add(vm);
        Optional<String> sshPublicKey = component.getPublicKey();

        // Check ssh public key
        if (sshPublicKey.isPresent()) {
            vm.setAuthentication(false, sshPublicKey.get());
            logger.info("Setting an ssh public key for vm " + vmName);
        }

        // Check OS
        Optional<String> osFamily = component.getOsFamily();

        if (osFamily.isPresent() && !osFamily.get().toLowerCase().contains("linux")) {
            throw new RuntimeException("Only linux-based operating systems are currently supported!");
        }

        Optional<String> image = component.getMachineImage();

        if (image.isPresent() && !image.get().toLowerCase().contains("ubuntu")) {
            throw new RuntimeException("Only Ubuntu OS is currently supported!");
        }

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
            sourceCompute.getDependsOn().add(String.format("Microsoft.Compute/virtualMachines/%s", targetCompute.getName()));
        }
    }

    @Override
    public void visit(RootComponent component) {
        collectIngressPorts(component);
        collectVmExtensions(component);
        component.setTransformed(true);
    }

    private void collectIngressPorts(RootComponent component) {
        component.getProperty(PORT).ifPresent(port -> {
            Optional<VirtualMachine> vm = this.getHostingVirtualMachine(component);
            vm.ifPresent(virtualMachine -> virtualMachine.addPort(component.getNormalizedName(), String.valueOf(port)));
        });
    }

    private void collectVmExtensions(RootComponent component) {
        Optional<VirtualMachine> vm = this.getHostingVirtualMachine(component);

        if (vm.isPresent()) {
            List<Pair<String, String>> artifacts = collectOperations(component);
            List<VirtualMachineExtension> existingExtensions = this.getExistingExtensions(vm.get());
            VirtualMachineExtension currentExtension;

            for (Pair<String, String> artifact : artifacts) {
                final String extensionName = String.format("%s_extension_%s", vm.get().getName(), artifact.getKey());
                // create new extension (script) and set its name
                currentExtension = new VirtualMachineExtension(extensionName);
                // set the path of the script file to be executed
                currentExtension.setScriptPath(artifact.getValue());
                // set dependencies for the vm extension
                List<String> dependencies = new ArrayList<>();
                // set a dependency on the virtual machine
                dependencies.add(String.format("Microsoft.Compute/virtualMachines/%s", vm.get().getName()));
                // set dependencies on previous scripts
                existingExtensions.forEach(extension -> dependencies.add(String.format("Microsoft.Compute/virtualMachines/extensions/%s", extension.getName())));
                currentExtension.setDependsOn(dependencies);
                // add extension to resources
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

        Optional<Operation> createOptional = component.getStandardLifecycle().getCreate();
        createOptional.ifPresent(operation -> this.addArtifacts(allArtifacts, "create", operation));
        component.getStandardLifecycle().getConfigure().ifPresent(operation -> this.addArtifacts(allArtifacts, "configure", operation));
        component.getStandardLifecycle().getStart().ifPresent(operation -> this.addArtifacts(allArtifacts, "start", operation));

        return allArtifacts;
    }

    private void addArtifacts(List<Pair<String, String>> existingArtifacts, String operationBaseName, Operation operation) {
        operation.getArtifacts().forEach(artifact ->
                existingArtifacts.add(ImmutablePair.of(String.format("%s_%s", operationBaseName, artifact.getNormalizedName()), artifact.getValue())));
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
        final String extensionNamePrefix = virtualMachine.getName() + "_extension_";
        return this.resultTemplate
                .getResources()
                .stream()
                .filter(resource -> resource.getName().contains(extensionNamePrefix) && resource instanceof VirtualMachineExtension)
                .map(resource -> (VirtualMachineExtension) resource)
                .collect(Collectors.toList());
    }
}
