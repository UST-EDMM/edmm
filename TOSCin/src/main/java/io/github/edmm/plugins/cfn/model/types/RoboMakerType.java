package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum RoboMakerType {
    Fleet(ComponentType.Compute),
    Robot(ComponentType.Compute),
    RobotApplication(ComponentType.Software_Component),
    RobotApplicationVersion(ComponentType.Software_Component),
    SimulationApplication(ComponentType.Software_Component),
    SimulationApplicationVersion(ComponentType.Software_Component);

    ComponentType componentType;

    RoboMakerType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
