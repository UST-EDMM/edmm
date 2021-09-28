package io.github.edmm.model.edimm;

import io.github.edmm.model.opentosca.TOSCABaseTypes;

public enum RelationType {
    DependsOn(TOSCABaseTypes.TOSCABaseRelationTypes.DependsOn),
    ConnectsTo(TOSCABaseTypes.TOSCABaseRelationTypes.ConnectsTo),
    HostedOn(TOSCABaseTypes.TOSCABaseRelationTypes.HostedOn);

    private final TOSCABaseTypes.TOSCABaseRelationTypes toscaRelationBaseType;

    RelationType(TOSCABaseTypes.TOSCABaseRelationTypes toscaRelationBaseType) {
        this.toscaRelationBaseType = toscaRelationBaseType;
    }

    public TOSCABaseTypes.TOSCABaseRelationTypes toToscaRelationBaseType() {
        return toscaRelationBaseType;
    }
}
