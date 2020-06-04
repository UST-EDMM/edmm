package io.github.edmm.model.edimm;

import io.github.edmm.model.opentosca.TOSCABaseTypes;

public class RelationTypes {

    public static final String relationDelimiter = "::";

    public enum RelationType {
        dependsOn(TOSCABaseTypes.TOSCARelationBaseTypes.DependsOn),
        connectsTo(TOSCABaseTypes.TOSCARelationBaseTypes.ConnectsTo);

        private final TOSCABaseTypes.TOSCARelationBaseTypes toscaRelationBaseType;

        RelationType(TOSCABaseTypes.TOSCARelationBaseTypes toscaRelationBaseType) {
            this.toscaRelationBaseType = toscaRelationBaseType;
        }

        public TOSCABaseTypes.TOSCARelationBaseTypes toToscaRelationBaseType() {
            return toscaRelationBaseType;
        }
    }
}
