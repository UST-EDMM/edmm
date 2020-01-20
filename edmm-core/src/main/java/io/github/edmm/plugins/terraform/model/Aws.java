package io.github.edmm.plugins.terraform.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

public class Aws {

    @Data
    public static class Beanstalk {

        private String name;
        private String filepath = "./";
        private String filename;
    }

    @Data
    public static class DbInstance {

        private String name;
        private String engine = "mysql";
        private String engineVersion = "5.7";
        private String instanceClass = "db.t2.micro";
        private String username = "user";
        private String password = "password";
    }

    @Data
    public static class Instance {

        private String name;
        private String ami;
        private String instanceType;
        private List<String> ingressPorts = new ArrayList<>();
        private List<FileProvisioner> fileProvisioners = new ArrayList<>();
        private List<RemoteExecProvisioner> remoteExecProvisioners = new ArrayList<>();
        private List<String> dependencies = new ArrayList<>();
        private Map<String, String> envVars = new HashMap<>();

        @Builder
        public Instance(String name, String ami, String instanceType) {
            this.name = name;
            this.ami = ami;
            this.instanceType = instanceType;
        }

        public void addIngressPort(String port) {
            ingressPorts.add(port);
        }

        public void addFileProvisioner(FileProvisioner provisioner) {
            fileProvisioners.add(provisioner);
        }

        public void addRemoteExecProvisioner(RemoteExecProvisioner provisioner) {
            remoteExecProvisioners.add(provisioner);
        }

        public void addDependency(String dependency) {
            dependencies.add(dependency);
        }

        public void addEnvVar(String name, String value) {
            envVars.put(name, value);
        }
    }
}
