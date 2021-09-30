package io.github.edmm.model.edimm;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class DeploymentInstance extends BasicInstance {
    private String name;
    private String createdAt;
    private String version;
    private InstanceState.InstanceStateForDeploymentInstance state;
    private List<ComponentInstance> componentInstances;

    private void createComponentInstances() {
        this.componentInstances = new ArrayList<>();
    }

    public void addToComponentInstances(ComponentInstance componentInstance) {
        if (this.componentInstances == null) {
            this.createComponentInstances();
        }
        this.componentInstances.add(componentInstance);
    }
}
