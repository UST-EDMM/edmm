package io.github.edmm.model.edimm;

import io.github.edmm.util.Constants;

// TODO mapping for a lot of properties, for now the ones used in case study are ok
public class PropertyKey {
    public enum Auth0 {
        domain(""),
        identifier(""),
        scopes(""),
        client_id(""),
        client_secret(""),
        region(""),
        type(Constants.TYPE);

        private String toscaPropertyKey;

        Auth0(String toscaPropertyKey) {
            this.toscaPropertyKey = toscaPropertyKey;
        }

        public String toTOSCAPropertyKey() {
            return this.toscaPropertyKey;
        }
    }

    public enum Compute {
        os_family(Constants.VMTYPE),
        instance_type(Constants.VMTYPE),
        key_name(Constants.VM_KEY_PAIR_NAME),
        public_key(Constants.VM_PUBLIC_KEY),
        private_key(Constants.VM_PRIVATE_KEY),
        public_address(Constants.VMIP),
        type(Constants.TYPE);

        private String toscaPropertyKey;

        Compute(String toscaPropertyKey) {
            this.toscaPropertyKey = toscaPropertyKey;
        }

        public String toTOSCAPropertyKey() {
            return this.toscaPropertyKey;
        }
    }

    public enum Database {
        schema_name(""),
        user(""),
        password(""),
        type(Constants.TYPE);

        private String toscaPropertyKey;

        Database(String toscaPropertyKey) {
            this.toscaPropertyKey = toscaPropertyKey;
        }

        public String toTOSCAPropertyKey() {
            return this.toscaPropertyKey;
        }
    }

    public enum Dbaas {
        instance_type(""),
        region(""),
        type(Constants.TYPE);

        private String toscaPropertyKey;

        Dbaas(String toscaPropertyKey) {
            this.toscaPropertyKey = toscaPropertyKey;
        }

        public String toTOSCAPropertyKey() {
            return this.toscaPropertyKey;
        }
    }

    public enum Dbms {
        port(""),
        root_password(""),
        type(Constants.TYPE);

        private String toscaPropertyKey;

        Dbms(String toscaPropertyKey) {
            this.toscaPropertyKey = toscaPropertyKey;
        }

        public String toTOSCAPropertyKey() {
            return this.toscaPropertyKey;
        }
    }

    public enum MysqlDatabase {
        schema_name(""),
        user(""),
        password(""),
        type(Constants.TYPE);

        private String toscaPropertyKey;

        MysqlDatabase(String toscaPropertyKey) {
            this.toscaPropertyKey = toscaPropertyKey;
        }

        public String toTOSCAPropertyKey() {
            return this.toscaPropertyKey;
        }
    }

    public enum MysqlDbms {
        port(""),
        root_password(""),
        type(Constants.TYPE);

        private String toscaPropertyKey;

        MysqlDbms(String toscaPropertyKey) {
            this.toscaPropertyKey = toscaPropertyKey;
        }

        public String toTOSCAPropertyKey() {
            return this.toscaPropertyKey;
        }
    }

    public enum Paas {
        region(""),
        archetype(""),
        min_instances(""),
        max_instances(""),
        type(Constants.TYPE);

        private String toscaPropertyKey;

        Paas(String toscaPropertyKey) {
            this.toscaPropertyKey = toscaPropertyKey;
        }

        public String toTOSCAPropertyKey() {
            return this.toscaPropertyKey;
        }
    }

    public enum Platform {
        region(""),
        type(Constants.TYPE);

        private String toscaPropertyKey;

        Platform(String toscaPropertyKey) {
            this.toscaPropertyKey = toscaPropertyKey;
        }

        public String toTOSCAPropertyKey() {
            return this.toscaPropertyKey;
        }
    }

    public enum Saas {
        region(""),
        client_id(""),
        client_secret(""),
        type(Constants.TYPE);

        private String toscaPropertyKey;

        Saas(String toscaPropertyKey) {
            this.toscaPropertyKey = toscaPropertyKey;
        }

        public String toTOSCAPropertyKey() {
            return this.toscaPropertyKey;
        }
    }

    public enum SoftwareComponent {
        type(Constants.TYPE);

        private String toscaPropertyKey;

        SoftwareComponent(String toscaPropertyKey) {
            this.toscaPropertyKey = toscaPropertyKey;
        }

        public String toTOSCAPropertyKey() {
            return this.toscaPropertyKey;
        }
    }

    public enum Tomcat {
        port(Constants.PORT),
        type(Constants.TYPE);

        private String toscaPropertyKey;

        Tomcat(String toscaPropertyKey) {
            this.toscaPropertyKey = toscaPropertyKey;
        }

        public String toTOSCAPropertyKey() {
            return this.toscaPropertyKey;
        }
    }

    public enum WebApplication {
        type(Constants.TYPE);

        private String toscaPropertyKey;

        WebApplication(String toscaPropertyKey) {
            this.toscaPropertyKey = toscaPropertyKey;
        }

        public String toTOSCAPropertyKey() {
            return this.toscaPropertyKey;
        }
    }

    public enum WebServer {
        port(Constants.PORT),
        type(Constants.TYPE);

        private String toscaPropertyKey;

        WebServer(String toscaPropertyKey) {
            this.toscaPropertyKey = toscaPropertyKey;
        }

        public String toTOSCAPropertyKey() {
            return this.toscaPropertyKey;
        }
    }

}
