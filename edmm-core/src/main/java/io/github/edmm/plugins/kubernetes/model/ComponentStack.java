package io.github.edmm.plugins.kubernetes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.component.RootComponent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class ComponentStack {

    private List<RootComponent> components = new ArrayList<>();

    private String baseImage;
    private Map<String, String> envVars = new HashMap<>();
    private List<FileMapping> artifacts = new ArrayList<>();
    private List<PortMapping> ports = new ArrayList<>();
    private List<FileMapping> operations = new ArrayList<>();
    private List<FileMapping> startOperations = new ArrayList<>();

    public ComponentStack(ComponentStack stack) {
        this.components = new ArrayList<>(stack.components);
        this.baseImage = stack.baseImage;
        this.envVars = new HashMap<>(stack.envVars);
        this.artifacts = new ArrayList<>(stack.artifacts);
        this.ports = new ArrayList<>(stack.ports);
        this.operations = new ArrayList<>(stack.operations);
        this.startOperations = new ArrayList<>(stack.startOperations);
    }

    public String getName() {
        return components.get(components.size() - 1).getName();
    }

    public String getLabel() {
        return getName().replace("_", "-");
    }

    public String getServiceName() {
        return getLabel() + "-service";
    }

    public String getDeploymentName() {
        return getLabel() + "-deployment";
    }

    public void addComponent(RootComponent component) {
        components.add(component);
    }

    public void addEnvVar(String name, String value) {
        envVars.put(name, value);
    }

    public void addArtifact(FileMapping mapping) {
        artifacts.add(mapping);
    }

    public void addPort(PortMapping mapping) {
        ports.add(mapping);
    }

    public void addOperation(FileMapping mapping) {
        operations.add(mapping);
    }

    public void addStartOperation(FileMapping mapping) {
        startOperations.add(mapping);
    }

    public boolean hasComponent(RootComponent component) {
        return components.contains(component);
    }
}
