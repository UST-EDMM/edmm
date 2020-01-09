package io.github.edmm.plugins.terraform.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.edmm.plugins.terraform.model.FileProvisioner;
import io.github.edmm.plugins.terraform.model.RemoteExecProvisioner;
import lombok.Builder;
import lombok.Data;

@Data
public class Ec2 {

    private String name;
    private String ami;
    private String instanceType;
    private List<String> ingressPorts;
    private List<FileProvisioner> fileProvisioners;
    private List<RemoteExecProvisioner> remoteExecProvisioners;
    private List<String> dependencies;
    private Map<String, String> envVars;

    public Ec2() {
        this.ingressPorts = new ArrayList<>();
        this.fileProvisioners = new ArrayList<>();
        this.remoteExecProvisioners = new ArrayList<>();
        this.dependencies = new ArrayList<>();
        this.envVars = new HashMap<>();
    }

    @Builder
    public Ec2(String name, String ami, String instanceType) {
        this();
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
