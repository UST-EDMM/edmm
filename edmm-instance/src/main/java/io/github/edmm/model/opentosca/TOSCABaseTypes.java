package io.github.edmm.model.opentosca;

/**
 * TOSCA Base types as of TOSCA Simple Profile Normative Types
 */
public class TOSCABaseTypes {
    public enum TOSCABaseRelationTypes {
        ConnectsTo,
        DependsOn,
        DeployedOn,
        HostedOn,
        AttachesTo,
        RoutesTo,
        BindsTo,
        LinksTo
    }

    public enum TOSCABaseNodeTypes {
        BlockStorage,
        Compute,
        ContainerApplication,
        ContainerRuntime,
        DBMS,
        Database,
        LoadBalancer,
        ObjectStorage,
        SoftwareComponent,
        WebApplication,
        WebServer
    }
}
