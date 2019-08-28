package io.github.edmm.plugins.terraform.model;

import lombok.Value;

@Value
public class FileProvisioner {

    private final String source;
    private final String destination;
}
