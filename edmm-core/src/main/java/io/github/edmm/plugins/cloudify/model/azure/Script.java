package io.github.edmm.plugins.cloudify.model.azure;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Script {
    private String name;
    private String path;
}
