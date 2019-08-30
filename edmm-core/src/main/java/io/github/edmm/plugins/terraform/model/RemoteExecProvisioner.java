package io.github.edmm.plugins.terraform.model;

import java.util.List;

import lombok.Value;

@Value
public class RemoteExecProvisioner {

    private List<String> scripts;
}
