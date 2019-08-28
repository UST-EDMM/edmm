package io.github.edmm.plugins.kubernetes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.component.RootComponent;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ComponentStack {

    private List<RootComponent> components = new ArrayList<>();

    private String baseImage;
    private Map<String, String> vars = new HashMap<>();
    private List<FileMapping> files = new ArrayList<>();
    private List<Integer> ports = new ArrayList<>();
    private List<String> cmd = new ArrayList<>();

    public ComponentStack(ComponentStack stack) {
        this.components = new ArrayList<>(stack.components);
        this.baseImage = stack.baseImage;
        this.vars = new HashMap<>(stack.vars);
        this.files = new ArrayList<>(stack.files);
        this.ports = new ArrayList<>(stack.ports);
        this.cmd = new ArrayList<>(stack.cmd);
    }

    public String getName() {
        return components.get(components.size() - 1).getName();
    }

    public void addComponent(RootComponent component) {
        components.add(component);
    }
}
