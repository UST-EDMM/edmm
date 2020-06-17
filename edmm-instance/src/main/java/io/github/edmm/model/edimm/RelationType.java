package io.github.edmm.model.edimm;

import io.github.edmm.model.opentosca.TOSCABaseTypes;

public enum RelationType {
    DependsOn(TOSCABaseTypes.TOSCARelationBaseTypes.DependsOn),
    ConnectsTo(TOSCABaseTypes.TOSCARelationBaseTypes.ConnectsTo),
    HostedOn(TOSCABaseTypes.TOSCARelationBaseTypes.HostedOn);

    private final TOSCABaseTypes.TOSCARelationBaseTypes toscaRelationBaseType;

    RelationType(TOSCABaseTypes.TOSCARelationBaseTypes toscaRelationBaseType) {
        this.toscaRelationBaseType = toscaRelationBaseType;
    }

    public TOSCABaseTypes.TOSCARelationBaseTypes toToscaRelationBaseType() {
        return toscaRelationBaseType;
    }

}
