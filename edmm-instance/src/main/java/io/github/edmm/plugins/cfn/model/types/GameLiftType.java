package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum GameLiftType {
    Alias(ComponentType.Software_Component),
    Build(ComponentType.Web_Server),
    Fleet(ComponentType.Compute),
    GameSessionQueue(ComponentType.Software_Component),
    MatchmakingConfiguration(ComponentType.Software_Component),
    MatchmakingRuleSet(ComponentType.Software_Component),
    Script(ComponentType.Software_Component);

    ComponentType componentType;

    GameLiftType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
