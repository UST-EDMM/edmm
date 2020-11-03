package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum BackupType {
    BackupPlan(ComponentType.Software_Component),
    BackupSelection(ComponentType.Software_Component),
    BackupVault(ComponentType.Software_Component);

    ComponentType componentType;

    BackupType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
