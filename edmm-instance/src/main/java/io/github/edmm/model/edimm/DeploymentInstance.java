package io.github.edmm.model.edimm;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class DeploymentInstance extends BasicInstance{
    private String name;
    private String createdAt;
    private String version;
    private InstanceState.InstanceStateForDeploymentInstance state;
    private List<ComponentInstance> componentInstances;
    private String deploymentModelLocation;

    /**
     * Create list of component instances for a deployment instance.
     */
    private void createComponentInstances() {
        this.componentInstances = new ArrayList<>();
    }

    /**
     * Add to existing list of component instances.
     *
     * @param componentInstance component instance to add
     */
    public void addToComponentInstances(ComponentInstance componentInstance) {
        if (this.componentInstances == null) {
            this.createComponentInstances();
        }
        this.componentInstances.add(componentInstance);
    }
}
