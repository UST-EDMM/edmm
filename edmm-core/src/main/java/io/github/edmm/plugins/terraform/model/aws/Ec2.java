package io.github.edmm.plugins.terraform.model.aws;

import java.util.List;

import io.github.edmm.plugins.terraform.model.Provisioner;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Ec2 {

    private String name;
    private String ami;
    private String instanceType;
    private List<String> ingressPorts;
    private List<Provisioner> provisioners;
    private List<String> dependsOn;
}
