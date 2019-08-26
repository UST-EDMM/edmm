package io.github.edmm.plugins.kubernetes.model;

import java.util.ArrayList;
import java.util.List;

import io.github.edmm.model.component.RootComponent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentStack {

    private List<RootComponent> components = new ArrayList<>();

    public ComponentStack(ComponentStack stack) {
        this.components = new ArrayList<>(stack.components);
    }

    public void addComponent(RootComponent component) {
        components.add(component);
    }

    public String getName() {
        return components.get(components.size() - 1).getName();
    }
}
