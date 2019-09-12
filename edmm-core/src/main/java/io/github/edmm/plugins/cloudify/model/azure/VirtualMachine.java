package io.github.edmm.plugins.cloudify.model.azure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private Map<String, String> environmentVariables;

    public void addIngressPort(String name, String port) {
        this.ingressPorts.add(new IngressPort(name, port));
    }

    public void addDependency(VirtualMachine dependency) {
        this.dependsOn.add(new Dependency(dependency.name));
    }

    public void addOperation(String componentName, String operationName, String scriptName, String scriptPath) {
        List<Operation> previous = new ArrayList<>(operations);
        Operation theOp = new Operation(operationName, componentName, previous);
        theOp.setScript(scriptName, scriptPath);
        this.getOperations().add(theOp);
    }
}
