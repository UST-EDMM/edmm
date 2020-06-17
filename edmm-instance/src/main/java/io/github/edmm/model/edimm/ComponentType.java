package io.github.edmm.model.edimm;

import io.github.edmm.model.opentosca.TOSCABaseTypes;

public enum ComponentType {
    AWS_Aurora(TOSCABaseTypes.TOSCABaseNodeTypes.Database),
    AWS_Beanstalk(TOSCABaseTypes.TOSCABaseNodeTypes.WebApplication),
    Auth0(TOSCABaseTypes.TOSCABaseNodeTypes.SoftwareComponent),
    Compute(TOSCABaseTypes.TOSCABaseNodeTypes.Compute),
    DBMS(TOSCABaseTypes.TOSCABaseNodeTypes.DBMS),
    DBaaS(TOSCABaseTypes.TOSCABaseNodeTypes.Database),
    Database(TOSCABaseTypes.TOSCABaseNodeTypes.Database),
    MySQL_DBMS(TOSCABaseTypes.TOSCABaseNodeTypes.DBMS),
    MySQL_Database(TOSCABaseTypes.TOSCABaseNodeTypes.Database),
    PaaS(TOSCABaseTypes.TOSCABaseNodeTypes.Container),
    Platform(TOSCABaseTypes.TOSCABaseNodeTypes.Container),
    SaaS(TOSCABaseTypes.TOSCABaseNodeTypes.SoftwareComponent),
    Software_Component(TOSCABaseTypes.TOSCABaseNodeTypes.SoftwareComponent),
    Tomcat(TOSCABaseTypes.TOSCABaseNodeTypes.WebServer),
    Web_Application(TOSCABaseTypes.TOSCABaseNodeTypes.WebApplication),
    Web_Server(TOSCABaseTypes.TOSCABaseNodeTypes.WebServer);

    private TOSCABaseTypes.TOSCABaseNodeTypes toscaBaseNodeType;

    ComponentType(TOSCABaseTypes.TOSCABaseNodeTypes toscaBaseNodeTypes) {
        this.toscaBaseNodeType = toscaBaseNodeTypes;
    }

    TOSCABaseTypes.TOSCABaseNodeTypes toTOSCABaseNodeType() {
        return this.toscaBaseNodeType;
    }
}
