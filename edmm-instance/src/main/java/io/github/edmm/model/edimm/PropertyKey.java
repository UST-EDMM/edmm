package io.github.edmm.model.edimm;

public class PropertyKey {
    public enum Auth0 {
        domain,
        identifier,
        scopes,
        client_id,
        client_secret,
        region,
        type
    }

    public enum Compute {
        os_family,
        machine_image,
        instance_type,
        key_name,
        public_key,
        public_address,
        type
    }

    public enum Database {
        schema_name,
        user,
        password,
        type
    }

    public enum Dbaas {
        instance_type,
        region,
        type
    }

    public enum Dbms {
        port,
        root_password,
        type
    }

    public enum MysqlDatabase {
        schema_name,
        user,
        password,
        type
    }

    public enum MysqlDbms {
        port,
        root_password,
        type
    }

    public enum Paas {
        region,
        archetype,
        min_instances,
        max_instances,
        type
    }

    public enum Platform {
        region,
        type
    }

    public enum Saas {
        region,
        client_id,
        client_secret,
        type
    }

    public enum SoftwareComponent {
        type
    }

    public enum Tomcat {
        port,
        type
    }

    public enum WebApplication {
        type
    }

    public enum WebServer {
        port,
        type
    }

}
