package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum SSMType {
    Association(ComponentType.Software_Component),
    Document(ComponentType.Software_Component),
    MaintenanceWindow(ComponentType.Software_Component),
    MaintenanceWindowTarget(ComponentType.Software_Component),
    MaintenanceWindowTask(ComponentType.Software_Component),
    Parameter(ComponentType.Software_Component),
    PatchBaseline(ComponentType.Software_Component),
    ResourceDataSync(ComponentType.Software_Component);

    ComponentType componentType;

    SSMType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
