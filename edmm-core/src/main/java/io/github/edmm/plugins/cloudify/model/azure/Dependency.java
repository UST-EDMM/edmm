package io.github.edmm.plugins.cloudify.model.azure;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Dependency {
    private String name;
}
