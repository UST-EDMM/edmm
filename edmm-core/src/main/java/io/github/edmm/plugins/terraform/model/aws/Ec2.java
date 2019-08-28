package io.github.edmm.plugins.terraform.model.aws;

import java.util.List;

import io.github.edmm.plugins.terraform.model.FileProvisioner;
import io.github.edmm.plugins.terraform.model.RemoteExecProvisioner;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Ec2 {

    private String name;
    private String ami;
    private String instanceType;
    private List<String> ingressPorts;
    private List<FileProvisioner> fileProvisioners;
    private List<RemoteExecProvisioner> remoteExecProvisioners;
    private List<String> dependencies;

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
}
