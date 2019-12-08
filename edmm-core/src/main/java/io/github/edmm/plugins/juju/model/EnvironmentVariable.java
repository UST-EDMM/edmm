package io.github.edmm.plugins.juju.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnvironmentVariable {
    private String name;
    private String value;
}
