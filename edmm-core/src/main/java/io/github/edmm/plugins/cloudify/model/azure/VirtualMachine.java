package io.github.edmm.plugins.cloudify.model.azure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VirtualMachine {
    private String name;
    private boolean passwordAuthentication;
    private String password;
    private String ssh;
    private List<IngressPort> ingressPorts;
    private List<Dependency> dependsOn;
    private List<Operation> operations;

    public void addIngressPort(String name, String port) {
        this.ingressPorts.add(new IngressPort(name, port));
    }

    public void addDependency(VirtualMachine dependency) {
        this.dependsOn.add(new Dependency(dependency.name));
    }

    public void addScript(String componentName, String operationName, String scriptName, String scriptPath) {
        Operation theOp;
        Optional<Operation> existing = operations.stream().filter(operation -> operation.getName().equals(operationName)).findFirst();

        if (existing.isPresent()) {
            theOp = existing.get();
        } else {
            theOp = new Operation(operationName, componentName);
            this.operations.add(theOp);
        }

        theOp.addScript(scriptName, scriptPath);
    }
}
